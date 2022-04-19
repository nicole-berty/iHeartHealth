package ie.ul.ihearthealth.login;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import ie.ul.ihearthealth.HomeActivity;
import ie.ul.ihearthealth.R;

/**
 * An activity for user registration using email and password
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView;
    private Button register;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.newUserEmail);
        passwordTextView = findViewById(R.id.newUserPassword);
        register = findViewById(R.id.newUserRegisterBtn);

        // Set on Click Listener on Registration button
        register.setOnClickListener(v -> registerNewUser());
    }

    private boolean isEmailValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private void registerNewUser()
    {

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // Validations for input email and password
        if (!isEmailValid(email)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter a valid email address",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (!(isPasswordValid(password))) {
            Toast.makeText(getApplicationContext(),
                    "Please enter a password that is at least 6 characters in length",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Registration successful!",
                                Toast.LENGTH_LONG)
                                .show();

                        // if the user created intent to login activity
                        Intent intent
                                = new Intent(RegisterActivity.this,
                                HomeActivity.class);
                        finishAffinity();
                        startActivity(intent);
                        finish();
                    }
                    else {
                        try
                        {
                            throw task.getException();
                        }
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                        {
                            Toast.makeText(RegisterActivity.this, "Incorrect email format", Toast.LENGTH_LONG).show();
                        }
                        catch (FirebaseAuthUserCollisionException existEmail)
                        {
                            Toast.makeText(RegisterActivity.this, "The provided email address is registered to another account", Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "Exception: " + e.getMessage());
                            // Registration failed
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Registration failed!"
                                            + " Please try again",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }
}