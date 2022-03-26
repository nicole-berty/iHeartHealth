package ie.ul.ihearthealth.ui.track_monitor;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ie.ul.ihearthealth.R;

public class TrackFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FirebaseFirestore db;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_measure, container, false);
        container.removeAllViews();
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        RadioButton nav_monitor = view.findViewById(R.id.nav_monitor_button);
        RadioButton nav_track = view.findViewById(R.id.nav_track_button);
        nav_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(((ViewGroup)(getView().getParent())).getId(), new MonitorFragment());
                fragmentTransaction.commit();
            }
        });
        nav_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(((ViewGroup)(getView().getParent())).getId(), new TrackFragment());
                fragmentTransaction.commit();
            }
        });

        EditText measurement = view.findViewById(R.id.measurement);

        Spinner measurementSpinner = (Spinner) view.findViewById(R.id.spinner);
        Spinner unitSpinner = (Spinner) view.findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.measurements_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        measurementSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> sodiumAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sodium_units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sodiumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> caloriesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.calorie_units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        caloriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> bpAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.bp_units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        bpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> alcoholAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.alcohol_units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        alcoholAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> tobaccoAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.tobacco_units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        tobaccoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> exerciseAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.exercise_units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Button submit = view.findViewById(R.id.submit);

        NumberPicker systolicValue = (NumberPicker) view.findViewById(R.id.systolicValue);
        systolicValue.setMinValue(0);
        systolicValue.setMaxValue(250);
        NumberPicker diastolicValue = (NumberPicker) view.findViewById(R.id.diastolicVal);
        diastolicValue.setMinValue(0);
        diastolicValue.setMaxValue(250);

        TextView bp_slash = view.findViewById(R.id.bp_forward_slash);

        TextView infoTv = view.findViewById(R.id.infoTv);
        infoTv.setMovementMethod(LinkMovementMethod.getInstance());

        measurementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = measurementSpinner.getSelectedItem().toString();
                systolicValue.setVisibility(View.INVISIBLE);
                diastolicValue.setVisibility(View.INVISIBLE);
                bp_slash.setVisibility(View.INVISIBLE);
                measurement.setVisibility(View.VISIBLE);
                submit.setEnabled(false);
                measurement.setHint("Value");
                switch (spinnerValue) {
                    case "Sodium":
                        unitSpinner.setAdapter(sodiumAdapter);
                        infoTv.setText(R.string.sodium_info);
                        break;
                    case "Calories":
                        unitSpinner.setAdapter(caloriesAdapter);
                        infoTv.setText(R.string.calories_info);
                        break;
                    case "Blood Pressure":
                        unitSpinner.setAdapter(bpAdapter);
                        systolicValue.setVisibility(View.VISIBLE);
                        diastolicValue.setVisibility(View.VISIBLE);
                        bp_slash.setVisibility(View.VISIBLE);
                        measurement.setVisibility(View.INVISIBLE);
                        systolicValue.setValue(120);
                        diastolicValue.setValue(90);
                        infoTv.setText(R.string.bp_info);
                        submit.setEnabled(true);
                        break;
                    case "Alcohol Intake":
                        unitSpinner.setAdapter(alcoholAdapter);
                        infoTv.setText(R.string.alcohol_info);
                        break;
                    case "Tobacco Intake":
                        unitSpinner.setAdapter(tobaccoAdapter);
                        infoTv.setText(R.string.tobacco_info);
                        break;
                    case "Exercise":
                        unitSpinner.setAdapter(exerciseAdapter);
                        infoTv.setText(R.string.exercise_info);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        systolicValue.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                submit.setEnabled(newVal > 0 && diastolicValue.getValue() > 0 && newVal > diastolicValue.getValue());
            }
        });

        diastolicValue.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker2, int oldVal, int newVal){
                submit.setEnabled(newVal > 0 && systolicValue.getValue() > 0 && newVal < systolicValue.getValue());
            }
        });

        measurement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                submit.setEnabled(measurement.getText().toString().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Map<String, String> data = new HashMap<>();
                measurement.onEditorAction(EditorInfo.IME_ACTION_DONE);
                Date currentDateTime = Calendar.getInstance().getTime();

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = format1.format(currentDateTime);

                String collection = measurementSpinner.getSelectedItem().toString();
                String[] splitCurrDateTime = currentDateTime.toString().split(" ");
                String currentDate = formattedDate;
                String currentTime = splitCurrDateTime[3];
                if(systolicValue.getVisibility() == View.VISIBLE) {
                    data.put(currentTime, systolicValue.getValue() + " " + unitSpinner.getSelectedItem().toString());
                    writeToDatabase("Systolic Blood Pressure", currentDate, data);
                    collection = "Diastolic Blood Pressure";
                    data.put(currentTime, diastolicValue.getValue() + " " + unitSpinner.getSelectedItem().toString());
                } else {
                    if(measurementSpinner.getSelectedItem().toString().equals("Exercise")) {
                        collection = measurementSpinner.getSelectedItem().toString() + " - " + unitSpinner.getSelectedItem().toString();
                    }
                    data.put(currentTime, measurement.getText().toString().replaceAll("\\s+","") + " " + unitSpinner.getSelectedItem().toString());
                }
                writeToDatabase(collection, currentDate, data);
                measurement.setText("");
            }
        });
    }

    public void writeToDatabase(String collection, String currentDate, Map data) {
        db.collection("inputData").document(user.getEmail()).collection(collection).document(currentDate)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        Toast.makeText(getContext(), "Measurement added successfully!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                        Toast.makeText(getContext(), "Sorry, that didn't work. Please try inputting the measurement again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}