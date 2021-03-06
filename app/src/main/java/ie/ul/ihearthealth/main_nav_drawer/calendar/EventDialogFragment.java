package ie.ul.ihearthealth.main_nav_drawer.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.time.LocalDate;
import java.util.Calendar;

import ie.ul.ihearthealth.R;

/**
 * A fragment for a DialogFragment that will allow the user to add and edit events on the calendar
 */
public class EventDialogFragment extends DialogFragment implements DialogInterface.OnShowListener {

        EventDialogListener listener;
        private Button timePickerButton;
        private Button datePickerButton;
        private TextView timeText;
        private TextView dateText;
        private EditText eventNameText;
        Boolean isNewEvent = true;
        String eventId, eventName, eventTime;
        LocalDate eventDate;
        LocalDate oldEventDate;
        Event event;

    @Override
    public void onShow(DialogInterface dialogInterface) {

    }

    public interface EventDialogListener{
            public void onDialogPositiveClick(DialogFragment dialog);
            public void onDialogNegativeClick(DialogFragment dialog);
    }

    public void setListener(EventDialogListener alertPositiveListener){
        this.listener = alertPositiveListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_calendar_event,null);
        timePickerButton = v.findViewById(R.id.pick_time_button);
        datePickerButton = v.findViewById(R.id.date_button);
        timeText = v.findViewById(R.id.preview_picked_time_textView);
        dateText = v.findViewById(R.id.appointment_date);
        eventNameText = v.findViewById(R.id.appointmentName);
        dateText.setText(eventDate.toString());

        if(event != null) {
            timeText.setText(eventTime);
            eventNameText.setText(eventName);
        }
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, currentHour, currentMinute, true);
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
                    timeText.setText(hour_string + ":" + minute_string);
                }
            };
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
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
                    dateText.setText(setYear + "-" + monthChoice + "-" + dayChoice);
                    eventDate = LocalDate.parse(dateText.getText().toString());
                }
            };
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because it's going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        listener.onDialogPositiveClick(EventDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(EventDialogFragment.this);
                    }
                });
        return builder.create();
    }
}