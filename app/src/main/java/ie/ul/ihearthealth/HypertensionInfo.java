package ie.ul.ihearthealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * An activity which contains a navigation drawer with fragments providing the user information about
 * hypertension
 */
public class HypertensionInfo extends AppCompatActivity {

    NavigationView navigationView;
    AppBarConfiguration mAppBarConfiguration;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hypertension_info);
        user = FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.ht_definition, R.id.htRisks, R.id.bpMeasurement, R.id.healthcareComms)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SharedPreferences sharedPref = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("activity", "info");
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void onStop() {
        super.onStop();
        SharedPreferences sharedPref = this.getSharedPreferences(
                "SharedPrefs", Context.MODE_PRIVATE);
        Intent i = null;
        if (!sharedPref.getBoolean("loggedOut", false) && sharedPref.getString("menuFragment", "reminder").equals("")) {
            i = new Intent(this, HomeActivity.class);
        } else if(sharedPref.getBoolean("loggedOut", false) && sharedPref.getString("menuFragment", "reminder").equals("")) {
            i = new Intent(this, LoginActivity.class);
        }
        if(i != null) startActivity(i);
        finish();
    }
}