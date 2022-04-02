package ie.ul.ihearthealth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import sdk.chat.core.session.ChatSDK;
import sdk.chat.firebase.adapter.module.FirebaseModule;
import sdk.chat.firebase.push.FirebasePushModule;
import sdk.chat.firebase.ui.FirebaseUIModule;
import sdk.chat.firebase.upload.FirebaseUploadModule;
import sdk.chat.ui.module.UIModule;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity_chat);

        SharedPreferences sharedPref = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        if(!sharedPref.getBoolean("chatOpenBefore", false)) {
            tryStop();
            startChat();
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("activity", "chat");
        editor.apply();

        openChat();

        Button chat = findViewById(R.id.button3);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChat();
            }
        });

    }

    void openChat() {
        ChatSDK.ui().startMainActivity(this);
    }

    boolean tryStop () {
        try {
            ChatSDK.ui().stop();
            return true;
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    void startChat() {
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
            UIModule.config().setAllowBackPressFromMainActivity(true);

           SharedPreferences sharedPref = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPref.edit();
           editor.putBoolean("chatOpenBefore", true);
           editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}