package ie.ul.ihearthealth.main_nav_drawer.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import ie.ul.ihearthealth.R;

/**
 * A fragment for medication reminders
 */
public class ReminderFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private TextView tv;
    private RecyclerView.Adapter adapter;
    private List<MedicineReminder> medicines;

    private Context mContext;

    // Initialise context from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reminder, container, false);
        container.removeAllViews();
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        tv = view.findViewById(R.id.textView3);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReminderActivity.class);
                startActivity(intent);
            }
        });

        medicines = new ArrayList<MedicineReminder>();
        recyclerView = view.findViewById(R.id.recycler_view_medicine);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new RecyclerAdapter(new ArrayList<>(), mContext);
        recyclerView.setAdapter(adapter);
        readFromDatabase();

        SharedPreferences sharedPref = mContext.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("activity", "main");
        editor.commit();
    }

    /**
     * A method to load medications from a list into a recyclerview on the fragment
     * @param data A list of MedicineReminder objects
     */
    public void loadMedicines(List<MedicineReminder> data) {
        medicines = data;
        recyclerView.setAdapter(new RecyclerAdapter(medicines, mContext));
        recyclerView.invalidate();
    }

    /**
     * A method to get a medicine list from given data
     * @param data An array of Strings containing medication reminders
     * @return A list of MedicineReminders
     */
    public List<MedicineReminder> getMedicineList(String[] data) {
        List<MedicineReminder> medicineList = new ArrayList<>();

        for(String s : data) {
            String[] splitReminder = s.split("=");
            if(splitReminder.length > 1) {
                String[] details = splitReminder[1].split(";");
                details[0] = details[0].replace("Medicine Name: ", "");
                MedicineReminder medicine;
                if(details.length > 6) {
                    medicine = new MedicineReminder(splitReminder[0].replace(" ", ""), details[0], details[1], details[2], details[3], details[4], details[5], details[6]);
                } else {
                    medicine = new MedicineReminder(splitReminder[0].replace(" ", ""), details[0], details[1], details[2], details[3], details[4], details[5], "");
                }
                medicineList.add(medicine);
            }
        }

        return medicineList;
    }

    /**
     * A method to read reminders from the database for a given user
     */
    private void readFromDatabase() {
        DocumentReference docRef = db.collection("reminders").document(user.getEmail());

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG", "Current data: " + snapshot.getData());
                    String allReminders = snapshot.getData().toString().replace("{", "");
                    allReminders = allReminders.replace("}", "");
                    String[] splitReminders = allReminders.split(",");
                    List<MedicineReminder> medicines = getMedicineList(splitReminders);
                    if(medicines.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.INVISIBLE);
                        loadMedicines(medicines);
                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                    recyclerView.setVisibility(View.INVISIBLE);
                    tv.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}