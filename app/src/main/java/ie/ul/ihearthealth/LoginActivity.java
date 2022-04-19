package ie.ul.ihearthealth;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import ie.ul.ihearthealth.login.LoginFragment;

/**
 * The launcher activity for the app which starts when the app first opens
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("loggedOut", true);
        editor.apply();
        editor.putString("activity", "login");

        editor.putBoolean("chatOpenBefore", false);
        editor.apply();

        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, LoginFragment.class, null)
                    .commit();
        }
    }
}