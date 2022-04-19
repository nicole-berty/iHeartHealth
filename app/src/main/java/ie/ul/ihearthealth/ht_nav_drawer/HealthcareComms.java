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
 * A fragment containing information on how to communicate with healthcare professionals
 */
public class HealthcareComms extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter myAdapter;
    private ArrayList<MyModel> myModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_healthcare_comms, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rec);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        myModelArrayList.add(new MyModel("<b>Is it normal to worry about communicating with medical professionals?</b>"));

        myModelArrayList.add(new MyModel("Many patients have concerns and fears that they are afraid to voice with their medical team. It is important to remember that medical professionals want to provide help and open, honest communication is key to ensuring that both you and your medical team are satisfied with the care provided. A lot of people feel anxious or stressed out when speaking with healthcare workers, with compounding issues such as education level and proficiency in the country's language contributing to this worry. Your healthcare team should be non-judgemental and aware of, and respectful of, your beliefs and feelings."));

        myModelArrayList.add(new MyModel("<b>How can I ensure that healthcare professionals listen to me?</b>"));
        myModelArrayList.add(new MyModel("<ul><li>Make sure you ask for help if you need it - whether that's with an interpreter, a social worker, or another healthcare worker, there's plenty of people willing to offer support.</li><li>Family and friends are also crucial to provide support and you can bring them along to medical appointments if you prefer.</li><li>Always remember that you can ask questions if you need clarification on medical terms or instructions.</li><li>It is a good idea to take notes during your visit so that you can review the details later.</li><li>Prepare yourself before any medical visit by making a list of your symptoms along with any questions that you may have.</li><ul>"));

        myModelArrayList.add(new MyModel("It is important that you are an active member of your healthcare team because you are the best advocate for your own health!"));

        myModelArrayList.add(new MyModel("<b>Sources</b>"));
        myModelArrayList.add(new MyModel("This information was obtained from the Hospital for Special Surgery (HSS) - the no. 1 orthopaedic hospital in the U.S. - and can be accessed at this location:<br> https://www.hss.edu/conditions_health-literacy-tips-improve-communication-with-healthcare-team.asp"));
        myModelArrayList.add(new MyModel(""));
        myAdapter = new MyAdapter(getActivity(), myModelArrayList);
        recyclerView.setAdapter(myAdapter);
    }
}