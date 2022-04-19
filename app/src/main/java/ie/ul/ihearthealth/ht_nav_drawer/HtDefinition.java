package ie.ul.ihearthealth.ht_nav_drawer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ie.ul.ihearthealth.R;

/**
 * A fragment containing information on high blood pressure
 */
public class HtDefinition extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter myAdapter;
    private ArrayList<MyModel> myModelArrayList = new ArrayList<>();
    public HtDefinition() {
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
        return inflater.inflate(R.layout.fragment_ht_definition, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rec);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        myModelArrayList.add(new MyModel("<b>What is Hypertension, also known as High Blood Pressure?</b>"));

        myModelArrayList.add(new MyModel("<b>High blood pressure</b>, clinically known as hypertension, occurs when the force of blood pushing against the walls of blood vessels as it is pumped by the heart is above a predefined level. Blood pressure is measured in millimetres of Mercury (mmHg) and readings include two values: the systolic pressure, which is the larger number on top, is the pressure of the blood when your heart contracts or beats, while the diastolic pressure is the pressure of the blood in between beats as your heart relaxes. Only one value needs to be raised above the normal threshold for it be considered high blood pressure. The systolic pressure should be below 140 mmHg while the diastolic blood pressure should be below 90 mmHg. If either value is above these thresholds on two separate occasions, hypertension can be diagnosed."));

        myModelArrayList.add(new MyModel("<b>How do you get hypertension?</b>"));

        myModelArrayList.add(new MyModel("There are a range of factors which contribute to the development of high blood pressure. The factors which can be controlled include: <br><ul><li> Unhealthy diet, high in sodium and low in fruit and vegetable intake</li><li> Lack of exercise</li><li> Tobacco and alcohol consumption</li><li> Being overweight or obese</li></ul>You can make changes to your lifestyle in order to reduce these risk factors and thus reduce your chance of developing high blood pressure.<br><br>Factors which can't be modified include:<br><ul><li> Family history of hypertension</li><li> Being older than 65</li><li> Co-existing diseases such as diabetes or kidney disease</li><ul>While you cannot change the non-modifiable risk factors, being aware of them enables you to monitor your blood pressure with your doctor."));

        myModelArrayList.add(new MyModel("<b>What are the consequences of high blood pressure?</b>"));

        myModelArrayList.add(new MyModel("High blood pressure has a range of serious consequences. The World Health Organisation estimates that this condition is responsible for 1 in 8 deaths worldwide and it directly or indirectly causes the deaths of 9 million people each year. It also greatly increases your risk of hospitalisation, cardiovascular disease, heart attack, congestive heart failure, stroke, kidney disease, death. Approximately 1.28 billion adults have this condition and because it can be fatal, it is critical that patients get the condition under control but, unfortunately, just 1 in 5 patients with high blood pressure have controlled it and nearly half of the adults with the condition are unaware that they have it as many adults do not regularly have their blood pressure measured."));

        myModelArrayList.add(new MyModel("<b>How can you treat and manage this condition?</b>"));

        myModelArrayList.add(new MyModel("Despite being quite a serious condition, high blood pressure can be treated using both medication and lifestyle changes. Medication will be prescribed by your doctor if they deem it medically necessary to decrease your blood pressure. Regardless of whether you are given medication, you will be advised to make lifestyle changes to reduce your blood pressure. The following changes will help to decrease blood pressure: <ul><li>Reduce sodium intake to less than 2g per day - this is equivalent to 5g salt per day</li><li> Decrease alcohol intake</li><li> Decrease tobacco intake</li><li> Keep a healthy weight</li><li> Be physically active - you should exercise for 150 minutes each week or about 30 minutes a day, 5 days a week</li><ul>These lifestyle modifications will help to keep your heart healthy and will reduce your blood pressure."));

        myModelArrayList.add(new MyModel("<b>Can you prevent yourself from getting high blood pressure?</b>"));

        myModelArrayList.add(new MyModel("It is possible to reduce the likelihood of getting hypertension by decreasing your modifiable risk factors. Carrying out the lifestyle changes mentioned in the previous answer will help to prevent high blood pressure in addition to reducing your blood pressure if it is already elevated."));

        myModelArrayList.add(new MyModel("<b>Sources</b>"));
        myModelArrayList.add(new MyModel("This information was obtained from the World Health Organization and can be accessed on their website at the following address: https://www.who.int/news-room/fact-sheets/detail/hypertension"));
        myModelArrayList.add(new MyModel(""));
        myAdapter = new MyAdapter(getActivity(), myModelArrayList);
        myAdapter.setTextSizes(14);
        recyclerView.setAdapter(myAdapter);
    }
}