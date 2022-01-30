package ie.ul.ihearthealth.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ie.ul.ihearthealth.R;
import ie.ul.ihearthealth.model.MyModel;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<MyModel> mStoreMyModelArrayList;


    public MyAdapter(Activity activity, ArrayList<MyModel> storeMyModelArrayList) {
        this.mContext = activity;
        this.mStoreMyModelArrayList = storeMyModelArrayList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyModel item = getValueAt(position);
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        if (item != null) {
            setupValuesInWidgets(myViewHolder, item);
        }
    }


    private MyModel getValueAt(int position) {
        return mStoreMyModelArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return mStoreMyModelArrayList.size();
    }

    private void setupValuesInWidgets(MyViewHolder itemHolder, MyModel
            myModel) {
        if (myModel != null) {
                itemHolder.title.setText(Html.fromHtml(myModel.getTitle()));
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView title;

        public MyViewHolder(View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.title);

        }
    }
}
