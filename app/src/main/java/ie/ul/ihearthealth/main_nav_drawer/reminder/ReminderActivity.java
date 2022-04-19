package ie.ul.ihearthealth.main_nav_drawer.reminder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ie.ul.ihearthealth.HomeActivity;
import ie.ul.ihearthealth.R;

/**
 * An activity for creating new Reminders
 */
public class ReminderActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser user;
    EditText medName;
    EditText medDosage;
    EditText medDesc;
    TextView time;
    TextView date;
    Switch repeatSwitch;
    RadioGroup radioGroup;
    Spinner dosageSpinner;
    RadioButton repeatAmount;
    Button submit;
    String currentDateTimeStr;
    String dataString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        setTitle("Create a Reminder");
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        medName = findViewById(R.id.medName);
        medDosage = findViewById(R.id.medDosage);
        medDesc = findViewById(R.id.medDesc);
        submit = findViewById(R.id.submit2);
        Button pickDate = findViewById(R.id.pick_date_button);
        Button pickTime = findViewById(R.id.pick_time_button);
        time = findViewById(R.id.preview_picked_time_textView);
        date = findViewById(R.id.preview_picked_date_textView);
        repeatSwitch = findViewById(R.id.repeatSwitch);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
        dosageSpinner = findViewById(R.id.dosageSpinner);

        SharedPreferences sharedPref = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("activity", "reminder");
        editor.apply();

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ReminderActivity.this,
                R.array.dosage_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dosageSpinner.setAdapter(adapter);

        medName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        medDosage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               setSubmitStatus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        medDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSubmitStatus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               setSubmitStatus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSubmitStatus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
        }

        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    ((RadioButton) radioGroup.getChildAt(i)).setEnabled(isChecked);
                }
                setSubmitStatus();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setSubmitStatus();
            }
        });

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(ReminderActivity.this, listener, currentHour, currentMinute, true);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String hour_string = hourOfDay < 10 ? //If hour is lower than 10, add a zero on the left
                            '0' + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
                    String minute_string = minute < 10 ? //If minute is lower than 10, add a zero on the left
                            '0' + String.valueOf(minute) : String.valueOf(minute);
                    time.setText(hour_string + ":" + minute_string);
                }
            };
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(ReminderActivity.this, listener, year, month, day);
                dialog.show();
            }

            private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int setYear, int setMonth, int setDay) {
                    String monthChoice;
                    String dayChoice;
                    setMonth += 1;
                    if (setMonth < 10) {
                        monthChoice = "0" + setMonth;
                    } else {
                        monthChoice = String.valueOf(setMonth);
                    }
                    if (setDay < 10) {
                        dayChoice = "0" + setDay;
                    } else {
                        dayChoice = String.valueOf(setDay);
                    }
                    date.setText(setYear + "-" + monthChoice + "-" + dayChoice);
                }
            };
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentDateTime = Calendar.getInstance().getTime();
                currentDateTimeStr = currentDateTime.toString().replace("GMT ", "");
                currentDateTimeStr = currentDateTimeStr.replace(" ", "-");
                Map<String, String> data = new HashMap<>();
                dataString = "Medicine Name: " + medName.getText().toString() + ";Medicine Dosage: " + medDosage.getText().toString()
                        + " " + dosageSpinner.getSelectedItem().toString() + ";Medicine Description: " + medDesc.getText().toString() + ";Time: "
                        + time.getText() + ";Start Date: " + date.getText();
                if(repeatSwitch.isChecked()) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    repeatAmount = (RadioButton) findViewById(selectedId);
                    dataString = dataString + ";Repeats: " + repeatAmount.getText();
                } else {
                   dataString += ";Repeats: None";
                }
                data.put(currentDateTimeStr, dataString);
                writeToDatabase(data);
            }
        });
    }

    /**
     * A method to write given data to the database
     * @param data A map of data containing a unique date key and a value string with the reminder
     *             details
     */
    void writeToDatabase(Map data) {
        Context context = this;
        db.collection("reminders").document(user.getEmail())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        Toast.makeText(ReminderActivity.this, "Reminder added successfully!", Toast.LENGTH_LONG).show();
                        setNotification();
                        SharedPreferences prefs = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
                        int id = prefs.getInt("notificationNumber", 0);
                        dataString = dataString + ";Id:" + id;
                        updateReminder();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                        Toast.makeText(ReminderActivity.this, "Sorry, that didn't work. Please try creating the reminder again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * A method to determine whether the submit button should be enabled based on the validity of the
     * fields in the activity
     */
    void setSubmitStatus() {
        if(repeatSwitch.isChecked()) {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            submit.setEnabled(isNumeric(medDosage.getText().toString()) && medName.getText().toString().length() > 0 &&
                    time.getText().toString().length() > 0 && date.getText().toString().length() > 0 && selectedId != -1);
        } else {
            submit.setEnabled(isNumeric(medDosage.getText().toString()) && medName.getText().toString().length() > 0 &&
                    time.getText().toString().length() > 0 && date.getText().toString().length() > 0);
        }
    }

    /**
     * A method to check whether a given string is numeric based on a pattern
     * @param str A string containing a potential number
     * @return A boolean representing whether the String contains a valid number
     */
    private boolean isNumeric(String str) {
        return str.matches("\\d+(\\.\\d+)?");
    }

    /**
     * A method to update a reminder in the database
     */
    private void updateReminder() {
        db.collection("reminders")
                .document(user.getEmail())
                .update(currentDateTimeStr, dataString);
    }

    /**
     * A method to set a notification for a reminder
     */
    private void setNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        String[] timeSplit = time.getText().toString().split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent notificationIntent = new Intent(ReminderActivity.this, AlarmBroadcast.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        notificationIntent.putExtra("medicineName", medName.getText().toString());
        SharedPreferences prefs = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        int requestCode = prefs.getInt("notificationRequestCode", 0);
        notificationIntent.putExtra("id", requestCode);
        dataString = dataString + ";RequestCode:" + requestCode;
        PendingIntent broadcast = PendingIntent.getBroadcast(ReminderActivity.this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        requestCode = requestCode + 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notificationRequestCode", requestCode);
        editor.apply();
        long uniqueId = calendar.getTimeInMillis();
        if(repeatAmount != null) {
            if(repeatAmount.getText().toString().equalsIgnoreCase("Twice Daily")) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, uniqueId, AlarmManager.INTERVAL_HALF_DAY, broadcast);
            } else if(repeatAmount.getText().toString().equalsIgnoreCase("Daily")) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, uniqueId, AlarmManager.INTERVAL_DAY, broadcast);
            } else if(repeatAmount.getText().toString().equalsIgnoreCase("Every Second Day")) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, uniqueId,  AlarmManager.INTERVAL_DAY * 2, broadcast);
            } else if(repeatAmount.getText().toString().equalsIgnoreCase("Weekly")) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, uniqueId,  AlarmManager.INTERVAL_DAY * 7, broadcast);
            } else if(repeatAmount.getText().toString().equalsIgnoreCase("Monthly")) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, uniqueId,  AlarmManager.INTERVAL_DAY * 28L, broadcast);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, uniqueId, broadcast);
        }
        dataString = dataString + ";RepeatID:" + uniqueId;
        Log.d("TAG", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences prefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        if(prefs.getBoolean("fromNotification", false)) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }
}