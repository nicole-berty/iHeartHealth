package ie.ul.ihearthealth.main_nav_drawer.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ie.ul.ihearthealth.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Medicine> medicines;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;

    RecyclerAdapter(List<Medicine> medicines, Context context) {
        this.medicines = medicines;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Medicine medicine = medicines.get(position);

        holder.medName.setText(medicine.getMedName());
        holder.medDose.setText(medicine.getMedDosage());
        holder.medDesc.setText(medicine.getMedDesc());
        holder.medTime.setText(medicine.getMedTime());
        holder.medStartDate.setText(medicine.getStartDate());
        holder.medRepeats.setText(medicine.getRepeat());

        holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = db.collection("reminders").document(user.getEmail());

                Map<String,Object> updates = new HashMap<>();
                updates.put(medicine.getId(), FieldValue.delete());

                docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        holder.cardView.setVisibility(View.GONE);
                        Toast.makeText(context, holder.medName.getText().toString() + " deleted", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        MaterialTextView medName, medDose, medDesc, medTime, medStartDate, medRepeats;
        MaterialCardView cardView;
        ImageButton imageButtonDelete;

        ViewHolder(View itemView)
        {
            super(itemView);

            medDose = itemView.findViewById(R.id.dosage_text_view);
            medName = itemView.findViewById(R.id.medicine_name_text_view);
            medDesc = itemView.findViewById(R.id.desc_text_view);
            medTime = itemView.findViewById(R.id.time_text_view);
            medStartDate = itemView.findViewById(R.id.start_date_text_view);
            medRepeats = itemView.findViewById(R.id.repeats_text_view);
            cardView = itemView.findViewById(R.id.card_view_medicine);
            imageButtonDelete = itemView.findViewById(R.id.medicine_delete_button);

        }
    }
}
