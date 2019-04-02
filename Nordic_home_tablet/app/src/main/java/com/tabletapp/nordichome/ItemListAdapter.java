package com.tabletapp.nordichome;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import no.nordicsemi.android.meshprovisioner.Group;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> {
    private ArrayList<Group> mDataset = new ArrayList<>();
    public static String TAG = ItemListAdapter.class.getSimpleName();
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        LinearLayout layout;

        public MyViewHolder(View v) {
            super(v);
            layout = v.findViewById(R.id.list_element);
            textView = v.findViewById(R.id.id_text);

            layout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItemListAdapter(AppCompatActivity context) {
        this.context = context;

    }

    public void addData(Group data){
        mDataset.add(data);
        notifyItemInserted(mDataset.size() - 1);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

