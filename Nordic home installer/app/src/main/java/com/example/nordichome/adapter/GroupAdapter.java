package com.example.nordichome.adapter;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.nordichome.ApplicationExtension;
import com.example.nordichome.GroupConfigActivity;
import com.example.nordichome.GroupsActivity;
import com.example.nordichome.ProvisioningActivity;
import com.example.nordichome.R;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import viewmodels.ProvisionedNodesViewmodes;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
    private ArrayList<Group> mDataset = new ArrayList<>();
    public static String TAG = Group.class.getSimpleName();
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        RelativeLayout layout;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.group_textView);
            layout = v.findViewById(R.id.json_item_box);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Group group = mDataset.get(getAdapterPosition());
                    Intent intent = new Intent(GroupAdapter.this.context, GroupConfigActivity.class);
                    intent.putExtra("group", group);
                    GroupAdapter.this.context.startActivity(intent);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupAdapter(Context context) {
        this.context = context;
    }

    public void addData(Group data) {
        mDataset.add(data);
        notifyItemInserted(mDataset.size() - 1);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);
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