package ie.ul.ihearthealth.ui.track_monitor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ie.ul.ihearthealth.R;

public class TrackFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TrackViewModel trackViewModel;
    private FirebaseFirestore db;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trackViewModel =
                new ViewModelProvider(this).get(TrackViewModel.class);
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

        TextView infoTv = view.findViewById(R.id.infoTv);
        infoTv.setMovementMethod(LinkMovementMethod.getInstance());

        measurementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = measurementSpinner.getSelectedItem().toString();
                switch (spinnerValue) {
                    case "Sodium":
                        unitSpinner.setAdapter(sodiumAdapter);
                        infoTv.setText(R.string.sodium_info);
                        break;
                    case "Calories":
                        unitSpinner.setAdapter(caloriesAdapter);
                        break;
                    case "Blood Pressure":
                        unitSpinner.setAdapter(bpAdapter);
                        break;
                    case "Alcohol Intake":
                        unitSpinner.setAdapter(alcoholAdapter);
                        break;
                    case "Tobacco Intake":
                        unitSpinner.setAdapter(tobaccoAdapter);
                        break;
                    case "Exercise":
                        unitSpinner.setAdapter(exerciseAdapter);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button submit = view.findViewById(R.id.submit);

        EditText measurement = view.findViewById(R.id.measurement);
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

        Map<String, String> data = new HashMap<>();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentDateTime = Calendar.getInstance().getTime();
                String[] splitCurrDateTime = currentDateTime.toString().split(" ");
                String currentDate = splitCurrDateTime[splitCurrDateTime.length - 1] + "-" + splitCurrDateTime[1] + "-" + splitCurrDateTime[2];
                String currentTime = splitCurrDateTime[3];
                data.put(currentTime, measurement.getText().toString() + " " + unitSpinner.getSelectedItem().toString());
                String collection = measurementSpinner.getSelectedItem().toString();
                writeToDatabase(collection, currentDate, data);
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
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