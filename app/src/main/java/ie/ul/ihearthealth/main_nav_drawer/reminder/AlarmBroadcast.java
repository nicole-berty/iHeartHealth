package ie.ul.ihearthealth.main_nav_drawer.reminder;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ie.ul.ihearthealth.HomeActivity;
import ie.ul.ihearthealth.R;

/**
 * An AlarmBroadcast which is used to create and set notifications for given times
 */
public class AlarmBroadcast extends BroadcastReceiver {
    private static final String CHANNEL_ID = "ie.ul.ihearthealth.notificationReminderId";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MedNotification.class);
        SharedPreferences sharedPref = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("menuFragment", "reminder");
        editor.apply();
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        int id = sharedPref.getInt("notificationNumber", 0);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.outline_medication_24)
                .setContentTitle("Reminder")
                .setContentText("It's time to take " + intent.getStringExtra("medicineName"))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("It's time to take " + intent.getStringExtra("medicineName")))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound((Uri)intent.getParcelableExtra("Ringtone"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medication Reminder ",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id, builder.build());
        id = id + 1;
        editor.putInt("notificationNumber", id);
        editor.commit();
    }
}