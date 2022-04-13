package ie.ul.ihearthealth.main_nav_drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.joda.time.DateTime;

import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ie.ul.ihearthealth.R;

public class GoogleFitFragment extends Fragment {
    private Context mContext;
    private TextView dailyStepCount;
    private TableLayout tl;
    private TextView noData;
    private List<DataSet> dbDatasets;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Button sendData;

    DataSource dataSource = new DataSource.Builder()
            .setAppPackageName("com.google.android.gms")
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .build();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                recordSteps();
                getStepCountDelta();
                getHistoricalStepData();
            }
        }
    }

    // Initialise context from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void recordSteps() {
        Fitness.getRecordingClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE);
    }


    public GoogleFitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_fit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tl = view.findViewById(R.id.tableLayout);
        noData = view.findViewById(R.id.noData);
        dailyStepCount = view.findViewById(R.id.daily_step_counter);
        sendData = view.findViewById(R.id.shareData);

        dbDatasets = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(hasPermissions()) {
            getStepCountDelta();
            getHistoricalStepData();
        } else {
            getPermissions();
        }

        Button revokeButton = view.findViewById(R.id.revokeButton);
        revokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasPermissions()) {
                    Fitness.getRecordingClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                            .unsubscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder().addExtension(getFitnessOptions()).build();
                    GoogleSignIn.getClient(mContext, signInOptions).revokeAccess();

                    Toast.makeText(mContext, "Permissions revoked, we'll stop reading your fitness data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStepsToDatabase();
            }
        });
    }

    private boolean hasPermissions() {
        FitnessOptions fitnessOptions = getFitnessOptions();
        if(GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(mContext), fitnessOptions)) {
            return true;
        }
        return false;
    }

    private void getPermissions() {
        GoogleSignIn.requestPermissions(
                getActivity(),
                123,
                GoogleSignIn.getLastSignedInAccount(mContext),
                getFitnessOptions());;
    }

    private void getHistoricalStepData() {
        if(!hasPermissions()) {
            getPermissions();
            return;
        }
        DateTime current = new DateTime().withTimeAtStartOfDay();
        DataReadRequest request = new DataReadRequest.Builder()
                .aggregate(dataSource)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(current.minusWeeks(1).getMillis(), current.getMillis(), TimeUnit.MILLISECONDS)
                .build();
        Fitness.getHistoryClient(getActivity(), GoogleSignIn.getLastSignedInAccount(mContext))
                .readData(request)
                .addOnSuccessListener(response -> {
                            for (Bucket bucket : response.getBuckets()) {
                                for (DataSet dataSet : bucket.getDataSets()) {
                                    if(dataSet.getDataPoints().size() < 1) {
                                       tl.setVisibility(View.INVISIBLE);
                                       noData.setVisibility(View.VISIBLE);
                                    } else {
                                        noData.setVisibility(View.INVISIBLE);
                                        tl.setVisibility(View.VISIBLE);
                                        dbDatasets.add(dataSet);
                                        dumpDataSet(dataSet);
                                    }

                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "There was a problem reading the historic data.", e);
                            }
                        });
    }

    private void addStepsToDatabase() {
        if(dbDatasets.size() > 0) {
            for(DataSet ds : dbDatasets) {
                for (DataPoint dp : ds.getDataPoints()) {
                    String stepsValue = "";
                    for (Field field : dp.getDataType().getFields()) {
                        stepsValue = dp.getValue(field).toString();
                    }
                    stepsValue += " Steps";
                    ZonedDateTime startDateTime = Instant.ofEpochMilli(dp.getStartTime(TimeUnit.MILLISECONDS)).atZone( ZoneId.of("UTC"));
                    DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
                    String date = startDateTime.format(dateFormatter);
                    String time = startDateTime.format(timeFormatter);
                    Map<String, String> data = new HashMap<>();
                    data.put(time, stepsValue);
                    writeToDatabase(date, data);
                }
            }
        } else {
            Toast.makeText(mContext, "You have no weekly step data to send", Toast.LENGTH_LONG).show();
        }
    }

    public void writeToDatabase(String currentDate, Map data) {
        db.collection("inputData").document(user.getEmail()).collection("Exercise - Steps").document(currentDate)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        Toast.makeText(getContext(), "Steps value added successfully!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                        Toast.makeText(getContext(), "Sorry, that didn't work. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void dumpDataSet(DataSet dataSet) {
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("en"));
        String[] dayNames = symbols.getShortWeekdays();

        TableRow tr = new TableRow(mContext);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        for (DataPoint dp : dataSet.getDataPoints()) {
            String stepsValue = "";
            for (Field field : dp.getDataType().getFields()) {
                stepsValue = dp.getValue(field).toString();
            }

            ZonedDateTime startDateTime = Instant.ofEpochMilli(dp.getStartTime(TimeUnit.MILLISECONDS)).atZone( ZoneId.of("UTC"));
            DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern(" dd MMMM");
            DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm O");
            String date = startDateTime.format(dateFormatter);
            String startTime = startDateTime.format(timeFormatter);

            ZonedDateTime endDateTime = Instant.ofEpochMilli(dp.getEndTime(TimeUnit.MILLISECONDS)).atZone( ZoneId.of("UTC"));
            String endTime = endDateTime.format(timeFormatter);

            TextView tv = new TextView(mContext);
            if(startDateTime.getDayOfWeek().getValue() + 1 == 8) {
                tv.setText(dayNames[1] + date);
            } else {
                tv.setText(dayNames[startDateTime.getDayOfWeek().getValue() + 1] + date);
            }

            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tv);
            TextView tv2 = new TextView(mContext);
            tv2.setText(startTime);
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tv2);
            TextView tv3 = new TextView(mContext);
            tv3.setText(endTime);
            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tv3);
            TextView tv4 = new TextView(mContext);
            tv4.setText(stepsValue);
            tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(tv4);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void getStepCountDelta() {
        if(!hasPermissions()) {
            getPermissions();
            return;
        }
        Fitness.getHistoryClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                .readDailyTotal(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                int total = dataSet.isEmpty() ? 0
                                        : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                dailyStepCount.setText(String.format("%d", total));
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error getting the step count for the user.", e);
                            }
                        });
    }

    private FitnessOptions getFitnessOptions() {
        // Request access to step count data from Fit history
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();
    }
}