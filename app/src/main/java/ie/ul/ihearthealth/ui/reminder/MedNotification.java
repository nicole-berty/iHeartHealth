package ie.ul.ihearthealth.ui.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import ie.ul.ihearthealth.HypertensionInfo;
import ie.ul.ihearthealth.LoginActivity;
import ie.ul.ihearthealth.MainActivity;
import ie.ul.ihearthealth.R;
import ie.ul.ihearthealth.ui.calendar.CalendarActivity;

public class MedNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_notifcation);

        TextView date = findViewById(R.id.textView7);
        TextView time = findViewById(R.id.textView8);

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        String hour_string = currentHour < 10 ? //If hour is lower than 10, add a zero on the left
                '0' + String.valueOf(currentHour) : String.valueOf(currentHour);
        String minute_string = currentMinute < 10 ? //If minute is lower than 10, add a zero on the left
                '0' + String.valueOf(currentMinute) : String.valueOf(currentMinute);
        time.setText(hour_string + ":" + minute_string);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month += 1;
        String monthChoice;
        String dayChoice;
        if (month < 10) {
            monthChoice = "0" + month;
        } else {
            monthChoice = String.valueOf(month);
        }
        if (day < 10) {
            dayChoice = "0" + day;
        } else {
            dayChoice = String.valueOf(day);
        }
        date.setText(year + "-" + monthChoice + "-" + dayChoice);

        Button confirm = findViewById(R.id.button2);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                SharedPreferences sharedPref = MedNotification.this.getSharedPreferences(
                        "SharedPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                switch (sharedPref.getString("activity", "main")) {
                    case "info":
                        i = new Intent(MedNotification.this, HypertensionInfo.class);
                        break;
                    case "reminder":
                        i = new Intent(MedNotification.this, ReminderActivity.class);
                        break;
                    case "calendar":
                        i = new Intent(MedNotification.this, CalendarActivity.class);
                        break;
                    case "login":
                        i = new Intent(MedNotification.this, LoginActivity.class);
                        break;
                    case "chat":
                    case "main":
                    default:
                        i = new Intent(MedNotification.this, MainActivity.class);
                        break;
                }

                editor.putBoolean("fromNotification", true);
                editor.putString("menuFragment", "");
                editor.apply();
                startActivity(i);
                finish();
            }
        });
    }
}