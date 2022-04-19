package ie.ul.ihearthealth.ht_nav_drawer;

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

/**
 * Adapter for the RecyclerViews on the hypertension information fragments
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<MyModel> myModelArrayList;
    private int textSize;

    public MyAdapter(Activity activity, ArrayList<MyModel> storeMyModelArrayList) {
        this.mContext = activity;
        this.myModelArrayList = storeMyModelArrayList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyModel item = getValueAt(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        if (item != null) {
            setupValuesInWidgets(myViewHolder, item);
        }
    }

    /**
     *  A method to set the text size in the recycler view
     * @param textSize An integer representing the size of the text
     */
    public void setTextSizes(int textSize) {
        this.textSize = textSize;
        notifyDataSetChanged();
    }


    private MyModel getValueAt(int position) {
        return myModelArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return myModelArrayList.size();
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
            title = itemView.findViewById(R.id.title);

        }
    }
}
