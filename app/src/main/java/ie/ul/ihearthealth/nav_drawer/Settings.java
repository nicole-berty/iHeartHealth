package ie.ul.ihearthealth.nav_drawer;

import static android.content.ContentValues.TAG;

import android.app.ActionBar;
import android.app.Dialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.nio.channels.AlreadyBoundException;
import java.util.Objects;

import ie.ul.ihearthealth.AlertDialogFragment;
import ie.ul.ihearthealth.LoginActivity;
import ie.ul.ihearthealth.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment implements AlertDialogFragment.AlertDialogListener {
    FirebaseUser user;
    boolean usingProvider = false;

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

        EditText email = view.findViewById(R.id.email_address);
        EditText pass = view.findViewById(R.id.password);
        EditText repeatPass = view.findViewById(R.id.password2);

        Button btn_change_email = view.findViewById(R.id.btn_change_email);
        Button btn_change_pass = view.findViewById(R.id.btn_change_pass);
        Button btn_delete_account = view.findViewById(R.id.btn_delete_account);

        user = mAuth.getCurrentUser();
        email.setText(user.getEmail());
        Toast.makeText(getContext(), user.getDisplayName(), Toast.LENGTH_LONG).show();
        final String[] strProvider = {""};
        mAuth.getAccessToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                strProvider[0] = getTokenResult.getSignInProvider();
            }
        });
        if (strProvider[0].equals("google.com") || strProvider[0].equals("facebook.com")) {
            usingProvider = true;
            TextView providerMessage = view.findViewById(R.id.providerText);
            providerMessage.setText("You cannot edit your email or password as you used a provider such as Google or Facebook to sign in.");
        }

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
                DialogFragment newFragment = new AlertDialogFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "reauthenticate");
            //   if(newFragment)
                user.updateEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                }
                            }
                        });
            }
        });
        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.updatePassword(pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User password updated.");
                                }
                            }
                        });
            }
        });

        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirmation dialog
                deleteAccount();
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
        dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Dialog dialogView = dialog.getDialog();
        EditText email = (EditText) dialogView.findViewById(R.id.username);
        Toast.makeText(getContext(), email.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }
}