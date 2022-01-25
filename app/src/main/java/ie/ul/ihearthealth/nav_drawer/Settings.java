package ie.ul.ihearthealth.nav_drawer;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import ie.ul.ihearthealth.AlertDialogFragment;
import ie.ul.ihearthealth.LoginActivity;
import ie.ul.ihearthealth.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment implements AlertDialogFragment.AlertDialogListener {
    FirebaseUser user;
    boolean usingProvider = false;
    String change = "Email";
    EditText email;
    EditText pass;
    AuthCredential credential;

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        email = view.findViewById(R.id.email_address);
        pass = view.findViewById(R.id.password);
        EditText repeatPass = view.findViewById(R.id.password2);

        Button btn_change_email = view.findViewById(R.id.btn_change_email);
        Button btn_change_pass = view.findViewById(R.id.btn_change_pass);
        Button btn_delete_account = view.findViewById(R.id.btn_delete_account);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        EditText name = view.findViewById(R.id.name);

        user = mAuth.getCurrentUser();
        email.setText(user.getEmail());
        if(user.getDisplayName() != null) name.setText(user.getDisplayName());
        String[] strProvider = {""};

        mAuth.getAccessToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                strProvider[0] = getTokenResult.getSignInProvider();
                if (strProvider[0].equals("google.com") || strProvider[0].equals("facebook.com")) {
                    usingProvider = true;
                    TextView providerMessage = view.findViewById(R.id.providerText);
                    providerMessage.setText("You cannot edit your email or password as you used a provider such as Google or Facebook to sign in.");
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!usingProvider) {
                    btn_change_email.setEnabled(email.getText().toString().trim().length() != 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btn_change_pass.setEnabled(pass.toString().length() > 6 && pass.getText().toString().equals(repeatPass.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        repeatPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btn_change_pass.setEnabled(repeatPass.toString().length() > 6 && repeatPass.getText().toString().equals(pass.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoticeDialog();
            }
        });
        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change = "pass";
                showNoticeDialog();
            }
        });

        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name.getText().toString())
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                } else {
                                    Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirmation dialog
                change = "delete";
                if(usingProvider) {
                    if (strProvider[0].equals("google.com")) {
                        GoogleSignInClient mGoogleSignInClient;
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken("988178322312-trph9390fjscnqq41i9a9vafn66kqsad.apps.googleusercontent.com")
                                .requestEmail()
                                .build();

                        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                        mGoogleSignInClient.silentSignIn()
                                .addOnCompleteListener(getActivity(),
                                        new OnCompleteListener<GoogleSignInAccount>() {
                                            @Override
                                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                                GoogleSignInAccount acct = task.getResult();
                                                // Get credential and reauthenticate that Google Account
                                                credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                                                reauthenticateAndTakeAction(credential);
                                            } // End onComplete
                                        });
                    } else if (strProvider[0].equals("facebook.com")) {
                        credential = FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().toString());
                        reauthenticateAndTakeAction(credential);
                    }
                } else {
                    showNoticeDialog();
                }
            }
        });
    }

    void deleteAccount() {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AlertDialogFragment();
        ((AlertDialogFragment) dialog).setListener(this);
        dialog.show(getChildFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText reauth_email = (EditText) dialog.getDialog().findViewById(R.id.username);
        EditText reauth_password = (EditText) dialog.getDialog().findViewById(R.id.password);

        if(reauth_email.getText().toString().length() < 1 || reauth_password.getText().toString().length() < 1) {
            Toast.makeText(getContext(), "Please fill in both fields", Toast.LENGTH_LONG).show();
        } else {
            credential = EmailAuthProvider
                        .getCredential(reauth_email.getText().toString(), reauth_password.getText().toString());
            reauthenticateAndTakeAction(credential);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    void reauthenticateAndTakeAction(AuthCredential credential) {
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "User re-authenticated.");
                        if(task.isSuccessful()){
                            if(change.equals("pass")) {
                                user.updatePassword(pass.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else if(change.equals("delete")) {
                                deleteAccount();
                            } else {
                                user.updateEmail(email.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_LONG).show();
                                                } else {
                                                    try
                                                    {
                                                        throw task.getException();
                                                    }
                                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                                    {
                                                        Toast.makeText(getContext(), "Incorrect email format", Toast.LENGTH_LONG).show();
                                                    }
                                                    catch (FirebaseAuthUserCollisionException existEmail)
                                                    {
                                                        Toast.makeText(getContext(), "That email is registered to another account", Toast.LENGTH_LONG).show();
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        Log.d(TAG, "Email change exception: " + e.getMessage());
                                                    }
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getContext(), "Re-authentication failed, please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}