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
import ie.ul.ihearthealth.adapter.MyAdapter;
import ie.ul.ihearthealth.model.MyModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BpMeasurement extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter myAdapter;
    private ArrayList<MyModel> myModelArrayList = new ArrayList<>();

    public BpMeasurement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bp_measurement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rec);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        myModelArrayList.add(new MyModel("<b>What does a blood pressure measurement involve?</b>"));

        myModelArrayList.add(new MyModel("Your general practitioner will take your blood pressure if you ask or arrange an appointment to do so. The reading can be taken in their office while sitting down using a device called a sphygmomanometer. The doctor will place an inflatable cuff around your bare arm which is attached to a pressure metre. The cuff will be inflated to its maximum point where no blood is able to flow through the air and you will feel a pressure in your arm as this happens. Then the air will be slowly released from the cuff and you will feel a release in the pressure. "));

        myModelArrayList.add(new MyModel("<b>Can I measure my blood pressure myself at home?</b>"));

        myModelArrayList.add(new MyModel("Yes, it is possible to purchase blood pressure monitors for use at home. These are straightforward, easy to use devices which involve placing a cuff on your bare arm and pressing a button on the machine to inflate the cuff. Once it has deflated again, you can simply note the reading from the machine. Your doctor may ask you to monitor your blood pressure at home for a period of time to see what your blood pressure is like in a different environment and at different times of the day."));

        myModelArrayList.add(new MyModel("Stride BP, a joint initiative between the European Society of Hypertension, the International Society of Hypertension, and the World Hypertension League provide lists and information for validated blood pressure monitors, which can be accessed at the following location: https://stridebp.org/bp-monitors. If you would like to see how home blood pressure monitoring works, please watch this video by the Irish Heart Foundation: https://www.youtube.com/watch?v=hsc4lqJNk9Y."));

        myModelArrayList.add(new MyModel(""));
        myAdapter = new MyAdapter(getActivity(), myModelArrayList);
        recyclerView.setAdapter(myAdapter);
    }
}