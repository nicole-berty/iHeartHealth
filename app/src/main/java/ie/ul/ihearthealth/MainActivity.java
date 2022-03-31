package ie.ul.ihearthealth;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.TimeUnit;

import ie.ul.ihearthealth.ui.calendar.CalendarActivity;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.firebase.adapter.module.FirebaseModule;
import sdk.chat.firebase.push.FirebasePushModule;
import sdk.chat.firebase.ui.FirebaseUIModule;
import sdk.chat.firebase.upload.FirebaseUploadModule;
import sdk.chat.ui.module.UIModule;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    GoogleApiClient mGoogleApiClient;
    FirebaseUser user;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "SharedPrefs", Context.MODE_PRIVATE);
        if(sharedPref.getInt("nightMode", 0) == 1) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
        try {
            ChatSDK.ui().stop();
        } catch (NullPointerException ignored) {
            try {
                ChatSDK.builder()
                        .setPublicChatRoomLifetimeMinutes(TimeUnit.HOURS.toMinutes(24))
                        .build()

                        // Add the Firebase network adapter module
                        .addModule(
                                FirebaseModule.builder()
                                        .setFirebaseRootPath("pre_1")
                                        .setFirebaseDatabaseURL("https://ihearthealth-f64c2-default-rtdb.europe-west1.firebasedatabase.app")
                                        .setDevelopmentModeEnabled(true)
                                        .build()
                        )

                        // Add the UI module
                        .addModule(UIModule.builder()
                                .setPublicRoomCreationEnabled(true)
                                .setPublicRoomsEnabled(true)
                                .build()
                        )

                        // Add modules to handle file uploads, push notifications
                        .addModule(FirebaseUploadModule.shared())
                        .addModule(FirebasePushModule.shared())

                        // Enable Firebase UI with phone and email auth
                        .addModule(FirebaseUIModule.builder()
                                .setProviders(EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID)
                                .build()
                        )
                        // Activate
                        .build()
                        .activate(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_measurements, R.id.nav_reminders, R.id.nav_google_fit, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        MenuItem calendar = navigationView.getMenu().findItem(R.id.nav_calendar);
        calendar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //do your stuff
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
                return true;
            }
        });

        MenuItem infoItem = navigationView.getMenu().findItem(R.id.nav_info);
        infoItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //do your stuff
                Intent intent = new Intent(getApplicationContext(), HypertensionInfo.class);
                startActivity(intent);
                return true;
            }
        });

        MenuItem chatItem = navigationView.getMenu().findItem(R.id.nav_chat);
        chatItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //do your stuff
                UIModule.config().setAllowBackPressFromMainActivity(true);
                ChatSDK.ui().startMainActivity(context);
                return true;
            }
        });

        navigationView.getMenu().findItem(R.id.logOut).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    123);
        } else {
            Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
        }

        String menuFragment = getIntent().getStringExtra("menuFragment");
        if (menuFragment != null) {
            if (menuFragment.equals("reminder")) {
                navController.navigate(R.id.nav_reminders);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = (TextView) headerView.findViewById(R.id.navEmail);
        navEmail.setText(user.getEmail());
        TextView navName = (TextView) headerView.findViewById(R.id.navName);
        if(user.getDisplayName() != null) navName.setText(user.getDisplayName());
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logout() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        FirebaseAuth.getInstance().signOut();
        // Log out of Facebook also
        LoginManager.getInstance().logOut();
        // Log out of Google
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        try {
            ChatSDK.ui().stop();
        } catch (NullPointerException ignored) {

        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getFragments())
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}