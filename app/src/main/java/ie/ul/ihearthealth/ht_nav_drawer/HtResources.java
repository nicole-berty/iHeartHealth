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
 * create an instance of this fragment.
 */
public class HtResources extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter myAdapter;
    private ArrayList<MyModel> myModelArrayList = new ArrayList<>();

    public HtResources() {
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
        return inflater.inflate(R.layout.fragment_ht_resources, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rec);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        myModelArrayList.add(new MyModel("<b>What resources are available to deal with hypertension?</b>"));

        myModelArrayList.add(new MyModel("There are many resources available to help you to learn about and manage high blood pressure. Your general practitioner should be your main point of contact regarding your health and you can ask your doctor take a blood pressure reading when you have an appointment - or you may schedule an appointment specifically to talk about your blood pressure if you are concerned."));

        myModelArrayList.add(new MyModel("<b>How can I learn more about high blood pressure?</b>"));

        myModelArrayList.add(new MyModel("There are many medical bodies which provide information for the general public on medical conditions. The World Health Organization is a trusted agency of the United Nations which is responsible for public health globally. The Irish Heart Foundation is an Irish charity which provides verified information and runs support groups for people who have or had heart problems. Croí is another Irish charity which aims to fight heart disease and stroke while providing educational and fitness programmes to the general public. These organisations can provide you with comprehensive information that is factual and medically correct. You can obtain this information online or you can contact the organisation if you would prefer to speak to a person directly for advice and support."));

        myModelArrayList.add(new MyModel("<b>Are there any resources to directly help me control my blood pressure?</b>"));

        myModelArrayList.add(new MyModel("If you have high blood pressure, your doctor will come up with a treatment plan for you which will include lifestyle changes and possibly medication. Applications such as iHeartHealth allow you to log your lifestyle changes and medical intake to help you control your health and also enable you to chat with other users who have the condition for support and encouragement, which are crucial components to following treatment plans."));

        myModelArrayList.add(new MyModel(""));
        myAdapter = new MyAdapter(getActivity(), myModelArrayList);
        recyclerView.setAdapter(myAdapter);
    }
}