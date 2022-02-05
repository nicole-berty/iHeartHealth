package ie.ul.ihearthealth;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import ie.ul.ihearthealth.ui.login.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    int darkMode;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        darkMode = sharedPref.getInt("nightMode", 0);
        if(darkMode == 1) AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, LoginFragment.class, null)
                    .commit();
        }
    }
}