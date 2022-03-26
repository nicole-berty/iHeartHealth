package ie.ul.ihearthealth.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;

import ie.ul.ihearthealth.R;

public class HomeFragment extends Fragment {
    private FirebaseUser user;
    private FirebaseFirestore db;
    TreeMap<LocalDate, String> systolicData;
    TreeMap<LocalDate, String> diastolicData;
    TextView lastReading;
    TextView recommendation;
    int systolicValue;
    int diastolicValue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        container.removeAllViews();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        systolicData = new TreeMap<LocalDate, String>();
        diastolicData = new TreeMap<LocalDate, String>();

        TextView welcome = view.findViewById(R.id.welcome);
        recommendation = view.findViewById(R.id.recommendation);

        if(user != null && user.getDisplayName() != null && !user.getDisplayName().equals("")) welcome.setText("Welcome, " + user.getDisplayName() + "!");
        if(user != null) {
            lastReading = view.findViewById(R.id.lastReading);
            recommendation.setText("");
            readFromDatabase("Systolic Blood Pressure", "Diastolic Blood Pressure");
        }
    }

    private String getLastValue(TreeMap<LocalDate,String> map, boolean isDiastolic) {
        String lastValue = "";
        String vals = map.lastEntry().getValue().replace("{", "");
        vals = vals.replace("}", "");
        vals = vals.replace(" mmHg", "");
        vals = vals.replace(" ", "");
        String[] timeVals = vals.split(",");
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
        TreeMap<LocalTime, String> times = new TreeMap<>();
        for(String t : timeVals) {
            String[] temp = t.split("=");
            String formattedTime = LocalTime.parse(temp[0]).format(myFormatObj);
            times.put(LocalTime.parse(formattedTime), temp[1]);
        }
        if(isDiastolic) {
            lastValue = times.lastEntry().getValue().toString() + " mmHg";
        } else {
            lastValue = times.lastEntry().getValue().toString();
        }
       return lastValue;
    }

    private void readFromDatabase(String collection, String collection2) {
        ArrayList<LocalDate> dates = new ArrayList<>();
        CollectionReference docRef = db.collection("inputData").document(user.getEmail()).collection(collection);
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    StringBuilder result = new StringBuilder();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", document.getId() + " => " + document.getData());
                        result.append(document.getId()).append(" ").append(document.getData()).append("\n");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            systolicData.put(LocalDate.parse(document.getId()), document.getData().toString());
                            dates.add(LocalDate.parse(document.getId()));
                        }
                    }
                    if(systolicData != null && systolicData.lastEntry() != null) {
                        String systolicVal = getLastValue(systolicData, false);
                        systolicValue = Integer.parseInt(systolicVal);
                        if (!collection2.equals("")) {
                            CollectionReference docRef2 = db.collection("inputData").document(user.getEmail()).collection(collection2);
                            docRef2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        StringBuilder result = new StringBuilder();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("TAG2", document.getId() + " => " + document.getData());
                                            result.append(document.getId()).append(" ").append(document.getData()).append("\n");
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                diastolicData.put(LocalDate.parse(document.getId()), document.getData().toString());
                                            }
                                        }
                                        String diastolicVal = getLastValue(diastolicData, true);
                                        diastolicValue = Integer.parseInt(diastolicVal.replace(" mmHg", ""));
                                        lastReading.setText(" \t\t\t" + systolicVal + "/" + diastolicVal + "\t\t\t\t ");
                                        updateRecommendation();
                                    }
                                }
                            });
                        }
                    } else {
                        lastReading.setText("\t\t\tNo data provided\t\t\t");
                        recommendation.setText("Please log some blood pressure measurements in the track & monitor section of the application to receive recommendations.");
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void updateRecommendation() {
        if(systolicValue < 140 && diastolicValue < 90) {
            recommendation.setText("Your blood pressure is within the ideal range - your systolic blood pressure is below 140 mmHg and your diastolic" +
                    " blood pressure is below 90 mmHg. To continue to maintain a healthy blood pressure, follow these tips: \n\n");
            recommendation.append(" \u2022Have your blood pressure measured at least annually by a healthcare professional\n");
            lastReading.setBackground(getResources().getDrawable(R.drawable.rounded_textview));
        } else {
            lastReading.setBackground(getResources().getDrawable(R.drawable.rounded_textview_red));
            recommendation.setText("Your blood pressure is above the ideal range - your blood pressure should be below 140/90 and if either one or both of the values are equal to or greater than this, your blood pressure is high. \n\nIf this was a measurement you took at home, and your blood" +
                    " pressure has been above 140/90 several times, you should have your blood pressure taken by a medical professional. " +
                    "Your doctor will come up with a treatment plan to manage your blood pressure, which will include lifestyle changes and" +
                    " possibly medication." +
                    " \n\nAside from contacting your doctor, you can follow some tips to lower you blood pressure now: \n\n");
            recommendation.append(" \u2022Take all medication as prescribed by your medical team - the reminders section of this app can help you track when you take your medication\n");
            recommendation.append(" \u2022Attend regular appointments with your doctor to monitor your blood pressure - the calendar tab will allow you to track your appointments\n");
        }
        recommendation.append(" \u2022Exercise regularly - at least 150 - 300 minutes of moderately intensive aerobic exercise weekly\n");
        recommendation.append(" \u2022Keep your daily sodium intake below 2500mg\n");
        recommendation.append(" \u2022Make sure your caloric intake matches your energy expenditure\n");
        recommendation.append(" \u2022Minimise your intake of alcohol and tobacco\n");

        recommendation.append("\n\n");
        recommendation.append(getResources().getString(R.string.source_who));
    }
}