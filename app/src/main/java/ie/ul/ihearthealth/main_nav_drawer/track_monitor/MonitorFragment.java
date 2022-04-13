package ie.ul.ihearthealth.main_nav_drawer.track_monitor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import ie.ul.ihearthealth.R;

public class MonitorFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private static Spinner dataSpinner;
    private static Spinner monthSpinner;
    private static Spinner yearSpinner;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private TextView details;
    private LineChart chart;
    private TextView tv2;
    private TextView tv4;
    private TextView averageDayVal;
    private ArrayList<Integer> years;
    private boolean highBp;

    public MonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_monitor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        highBp = false;

        tv2 = view.findViewById(R.id.textView2);
        tv4 = view.findViewById(R.id.textView4);
        averageDayVal = view.findViewById(R.id.averageMonthVal);
        RadioButton nav_monitor = view.findViewById(R.id.nav_monitor_button);
        RadioButton nav_track = view.findViewById(R.id.nav_track_button);
        nav_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), new MonitorFragment());
                fragmentTransaction.commit();
            }
        });
        nav_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), new TrackFragment());
                fragmentTransaction.commit();
            }
        });

        dataSpinner = (Spinner) view.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.graph_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dataSpinner.setAdapter(adapter);

        monthSpinner = (Spinner) view.findViewById(R.id.monthSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        monthSpinner.setAdapter(adapter2);

        HashMap<String, Integer> monthMap = new HashMap<>();
        for(int i = 0; i < getResources().getStringArray(R.array.months_array).length; i++) {
            monthMap.put(getResources().getStringArray(R.array.months_array)[i], i+1);
        }

        yearSpinner = (Spinner) view.findViewById(R.id.yearSpinner);

        years = new ArrayList<>();
        addToYears(0);
        addToYears(-1);
        addToYears(-2);
        addToYears(1);
        Collections.sort(years);
        ArrayAdapter<Integer> adapter3 = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, years);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter3);
        yearSpinner.setSelection(2);

        details = view.findViewById(R.id.detailsText);
        dataSpinner.setSelection(0);
        dataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                monthSpinner.setSelection(0);
                if(!(chart == null)) chart.clear();
                if(dataSpinner.getSelectedItem().toString().equals("Blood Pressure")) {
                    readFromDatabase("Systolic " + dataSpinner.getSelectedItem().toString(), "Diastolic " + dataSpinner.getSelectedItem().toString(), view,
                            monthMap.get(monthSpinner.getSelectedItem().toString()),
                            Integer.valueOf(yearSpinner.getSelectedItem().toString()));
                } else {
                    readFromDatabase(dataSpinner.getSelectedItem().toString(), "", view, monthMap.get(monthSpinner.getSelectedItem().toString()),
                            Integer.valueOf(yearSpinner.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        monthSpinner.setSelection(0);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int i, long l) {
                if(!(chart == null)) chart.clear();
                if(dataSpinner.getSelectedItem().toString().equals("Blood Pressure")) {
                    readFromDatabase("Systolic " + dataSpinner.getSelectedItem().toString(), "Diastolic " + dataSpinner.getSelectedItem().toString(), view,
                            monthMap.get(monthSpinner.getSelectedItem().toString()),
                            Integer.valueOf(yearSpinner.getSelectedItem().toString()));
                } else {
                    readFromDatabase(dataSpinner.getSelectedItem().toString(), "", view, monthMap.get(monthSpinner.getSelectedItem().toString()),
                            Integer.valueOf(yearSpinner.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int i, long l) {
                monthSpinner.setSelection(0);
                if(dataSpinner.getSelectedItem().toString().equals("Blood Pressure")) {
                    readFromDatabase("Systolic " + dataSpinner.getSelectedItem().toString(), "Diastolic " + dataSpinner.getSelectedItem().toString(), view,
                            monthMap.get(monthSpinner.getSelectedItem().toString()),
                            Integer.valueOf(yearSpinner.getSelectedItem().toString()));
                } else {
                    readFromDatabase(dataSpinner.getSelectedItem().toString(), "", view, monthMap.get(monthSpinner.getSelectedItem().toString()),
                            Integer.valueOf(yearSpinner.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void addToYears(int extraNum) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, extraNum);
        years.add(cal.get(Calendar.YEAR));
    }

    static String getUnits() {
        switch (dataSpinner.getSelectedItem().toString()) {
            case "Sodium":
                return "milligrams";
            case "Calories":
                return "kcal";
            case "Exercise - Steps":
                return "steps";
            case "Exercise - Minutes":
                return "minutes";
            case "Exercise - Hours":
                return "hours";
            case "Blood Pressure":
                return "mmHg";
            case "Alcohol Intake":
                return "Standard Units";
            case "Tobacco Intake":
                return "Cigarettes";
            default:
                return "units";
        }
    }

    static String getPointDetails(int day, float value) {
        String details = "Date: " + day + " " + monthSpinner.getSelectedItem().toString() + " " + yearSpinner.getSelectedItem().toString() + "\n Value: " + value + " ";
        details += getUnits();
        return details;
    }
    @SuppressLint("NewApi")
    public void groupLineChart(View view, Map<LocalDate, String> graphData, Map<LocalDate, String> graphData2, int month, int year, int chartID){
        chart = view.findViewById(chartID);
        chart.setOnChartValueSelectedListener(this);

        LineData chartData = new LineData();
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(14);

        if(graphData2 != null) {
            LegendEntry l1=new LegendEntry("Systolic",Legend.LegendForm.CIRCLE,10f,2f,null,Color.RED);
            LegendEntry l2=new LegendEntry("Diastolic", Legend.LegendForm.CIRCLE,10f,2f,null,Color.BLUE);
            l.setCustom(new LegendEntry[]{l1,l2});
            l.setEnabled(true);
            getMapData(graphData, year, month, chartData, true, true);
            getMapData(graphData2, year, month, chartData, true, false);
        } else {
            getMapData(graphData, year, month, chartData, false, false);
        }

        if(chartData.getDataSetCount() < 1) {
            chart.setVisibility(View.INVISIBLE);
            details.setText("You haven't logged any measurements for this month!");
            averageDayVal.setText("");
            tv2.setText("");
            tv4.setText("");
        } else {
            details.setText("");
            chart.setVisibility(View.VISIBLE);

            tv2.setText("Day of Month");
            tv4.setText("Value");

            chart.setDrawGridBackground(false);
            chart.getDescription().setEnabled(false);
            chart.setDrawBorders(false);

            chart.getAxisLeft().setEnabled(true);
            chart.getAxisLeft().setDrawAxisLine(true);
            chart.getAxisLeft().setTextSize(14);
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisRight().setEnabled(false);
            chart.getXAxis().setDrawAxisLine(true);
            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setTextSize(14);

            chart.getAxisLeft().setAxisLineWidth(3);
            chart.getXAxis().setAxisLineWidth(3);
            // enable touch gestures
            chart.setTouchEnabled(true);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            chart.setPinchZoom(false);

            chartData.setValueTextSize(12);
            chart.setExtraLeftOffset(10);
            chart.setExtraBottomOffset(10);

            chart.getXAxis().setAxisMinimum(0);
            chart.getXAxis().setAxisMaximum(31);

            IMarker marker = new MyMarkerView(getContext(), R.layout.my_marker_view);
            chart.setMarker(marker);

            chart.setData(chartData);
            chart.invalidate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getMapData(Map<LocalDate, String> graphData, int year, int month, LineData chartData, boolean isBPGraph, boolean isRed) {
        ArrayList<Integer> coloursUsed = new ArrayList<>();
        double dailyAverage = 0.0;
        int numDays = 0;
        int numMeasurements = 0;
        for (Map.Entry<LocalDate, String> set : graphData.entrySet()) {
            ArrayList<Entry> entries = new ArrayList<>();
            int day = set.getKey().getDayOfMonth();
            int month2 = set.getKey().getMonthValue();
            int year2 = set.getKey().getYear();
            if(year2 == year) {
                if (month2 == month) {
                    numDays = numDays + 1;
                    String[] temp = set.getValue().split(",");
                    for (String s : temp) {
                        String[] tempData = s.split("=");
                        String[] data = tempData[1].split(" ");
                        entries.add(new Entry(day, Float.parseFloat(data[0].replace("}", ""))));
                        numMeasurements++;
                        dailyAverage += Float.parseFloat(data[0].replace("}", ""));
                    }
                    LineDataSet set1 = new LineDataSet(entries, set.getKey().toString());
                    set1.setCircleRadius(5);
                    set1.setDrawHighlightIndicators(false);

                    if(isBPGraph) {
                        if(isRed) {
                            set1.setCircleColor(Color.RED);
                            set1.setColor(Color.RED);
                        } else {
                            set1.setCircleColor(Color.BLUE);
                            set1.setColor(Color.BLUE);
                        }
                    } else {
                        Random rnd = new Random();
                        int colour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        while (coloursUsed.contains(colour)) {
                            colour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        }
                        coloursUsed.add(colour);
                        set1.setCircleColor(colour);
                        set1.setColor(colour);
                    }
                    chartData.addDataSet(set1);
                }
            }
        }
        String days = "days";
        if(numDays == 1) {
            days = "day";
        } else if(!isBPGraph) {
            dailyAverage = dailyAverage / numDays;
        }
        if(isBPGraph) {
            if(numDays > 1) {
                dailyAverage = dailyAverage / numMeasurements;
            }
            if(isRed) {
                averageDayVal.append("Average daily blood pressure: " + String.format("%.2f", dailyAverage) + "/");
            } else {
                averageDayVal.append(String.format("%.2f", dailyAverage) + getUnits() + " based on values logged from " + numMeasurements + " measurements.");
            }

        } else if(!isRed) {
            averageDayVal.setText("Average daily intake: " + String.format("%.2f", dailyAverage) + " " + getUnits() + " based on values logged for " + numDays + " " + days + ".");
        }
        switch (dataSpinner.getSelectedItem().toString()) {
            case "Sodium":
                if(dailyAverage < 2500) {
                    averageDayVal.append("\n\nYour sodium intake is ideal - it is below the maximum recommended amount of 2500 milligrams a day. Keep up the good work!");
                } else {
                    averageDayVal.append("\n\nYour sodium intake is above the maximum recommended amount of 2500 milligrams a day. Regularly exceeding this intake can contribute" +
                            " to the development of high blood pressure and other cardiac problems. Try to reduce your sodium intake by avoiding adding table salt to meals," +
                            " opting for low or no sodium items when eating, cutting back on processed and smoked foods, and using herbs and spices to add flavour to food.");
                }
                break;
            case "Calories":
                if(dailyAverage < 2500) {
                    averageDayVal.append("\n\nYour caloric intake seems to be good - though it's important to remember that how many calories you need depends on your exercise levels, age, and sex." +
                            " In general, your caloric intake should be equal to your energy expenditure. ");
                } else {
                    averageDayVal.append("\n\nYour caloric intake is above the maximum recommended amount of 2500 kcal a day.  - though it's important to remember that how many calories you need depends on your exercise levels, age, and sex.\n" +
                            "\nYour caloric intake may be okay based on these factors - in general, your caloric intake should be equal to your energy expenditure." +
                            " If you are trying to lose weight, your caloric intake should be less than your energy expenditure. Being overweight contributes to the development of high blood pressure so " +
                            "it is important to maintain a healthy weight.");
                }
                break;
            case "Exercise - Steps":
                averageDayVal.setText("Average daily amount: " + String.format("%.2f", dailyAverage) + " " + getUnits() + " based on values logged for " + numDays + " " + days + ".");
                if(dailyAverage >= 10000) {
                    averageDayVal.append("\n\nYour exercise levels in steps are great! Keep up the good work - exercise helps to maintain a healthy weight, reduces blood pressure and resting heart rate, and is good for your mental health!");
                } else {
                    averageDayVal.append("\n\nYour exercise levels are below the recommended daily amount. You should aim to complete 150 - 300 minutes of moderate intensity aerobic activity per week - this helps to " +
                            "maintain a healthy weight, reduces blood pressure and resting heart rate, and is good for your mental health!");
                }
                break;
            case "Exercise - Minutes":
                averageDayVal.setText("Average daily amount: " + String.format("%.2f", dailyAverage) + " " + getUnits() + " based on values logged for " + numDays + " " + days + ".");
                if(dailyAverage > 30) {
                    averageDayVal.append("\n\nYour exercise levels in minutes are great! Keep up the good work - exercise helps to maintain a healthy weight, reduces blood pressure and resting heart rate, and is good for your mental health!");
                } else {
                    averageDayVal.append("\n\nYour exercise levels are below the recommended daily amount. You should aim to complete 150 - 300 minutes of moderate intensity aerobic activity per week - this helps to " +
                            "maintain a healthy weight, reduces blood pressure and resting heart rate, and is good for your mental health!");
                }
                break;
            case "Exercise - Hours":
                averageDayVal.setText("Average daily amount: " + String.format("%.2f", dailyAverage) + " " + getUnits() + " based on values logged for " + numDays + " " + days + ".");
                if(dailyAverage >= 1) {
                    averageDayVal.append("\n\nYour exercise levels in hours are great! Keep up the good work - exercise helps to maintain a healthy weight, reduces blood pressure and resting heart rate, and is good for your mental health!");
                } else {
                    averageDayVal.append("\n\nYour exercise levels are below the recommended daily amount. You should aim to complete 150 - 300 minutes of moderate intensity aerobic activity per week - this helps to " +
                            "maintain a healthy weight, reduces blood pressure and resting heart rate, and is good for your mental health!");
                }
                break;
            case "Blood Pressure":
                if((isRed && dailyAverage < 140) || (!isRed && dailyAverage < 90)) {
                    if(!isRed) averageDayVal.append("\n\nYour blood pressure is ideal - it is below 140/90 mmHg. Keep up the good work!");
                } else {
                    if(highBp) {
                        averageDayVal.append("\n\nYour blood pressure is high because the systolic value is equal to or above 140 mmHg and/or the diastolic value is equal to or greater than 90 mmHg. You should continue to monitor" +
                                " your blood pressure and if your doctor is not already regularly measuring your blood pressure, you should arrange an appointment to have your blood pressure taken. You can also make lifestyle changes " +
                                "to decrease your blood pressure - exercising regularly, maintaining a healthy weight, eating a diet low in sodium, and avoiding tobacco and alcohol will all help to decrease it.");
                        highBp = false;
                    }
                    highBp = true;
                }
                break;
            case "Alcohol Intake":
                if(dailyAverage <= 2) {
                    averageDayVal.append("\n\nYour alcohol intake is below the maximum recommended amount, though alcohol is associated with many health risks, both long and short time, so it is advised to minimise your alcoholic intake as much as possible.");
                } else {
                    averageDayVal.append("\n\nYour alcohol intake is above the maximum recommended amount - alcohol is associated with a range of health risks and contributes to high blood pressure. You should aim to reduce or eliminate your alcohol intake by " +
                            "choosing non alcoholic drink options when available.");
                }
                break;
            case "Tobacco Intake":
                averageDayVal.append("\n\nAs tobacco is associated with so many health risks, there is no maximum recommended amount - you should try to reduce or eliminate your tobacco usage by choosing non-tobacco based products, using nicotine replacement" +
                        " therapy or other medications if need be, and availing of support options from your medical team and the wider community");
                break;
            default:
                averageDayVal.append("");
                break;
        }
    }

    private void readFromDatabase(String collection, String collection2, View view, int month, int year) {
        CollectionReference docRef = db.collection("inputData").document(user.getEmail()).collection(collection);
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<LocalDate, String> graphData = new TreeMap<LocalDate, String>();
                    Map<LocalDate, String> graphData2 = new TreeMap<LocalDate, String>();
                    StringBuilder result = new StringBuilder();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", document.getId() + " => " + document.getData());
                        result.append(document.getId()).append(" ").append(document.getData()).append("\n");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            graphData.put(LocalDate.parse(document.getId()), document.getData().toString());
                        }
                    }
                    if(result.length() == 0) {
                        details.setText("You haven't logged any measurements for this item!");
                        averageDayVal.setText("");
                    } else {
                        if(!collection2.equals("")) {
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
                                                graphData2.put(LocalDate.parse(document.getId()), document.getData().toString());
                                            }
                                        }
                                        groupLineChart(view, graphData, graphData2, month, year, R.id.chart1);
                                    }
                                }
                            });
                        } else {
                            groupLineChart(view, graphData, null, month, year, R.id.chart1);
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            chart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart long pressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart fling. VelocityX: " + velocityX + ", VelocityY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

    }
}