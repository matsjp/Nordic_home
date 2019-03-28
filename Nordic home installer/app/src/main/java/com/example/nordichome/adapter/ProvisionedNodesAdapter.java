package com.example.nordichome.adapter;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.nordichome.ApplicationExtension;
import com.example.nordichome.ProvisioningActivity;
import com.example.nordichome.R;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import viewmodels.ProvisionedNodesViewmodes;

public class ProvisionedNodesAdapter extends RecyclerView.Adapter<ProvisionedNodesAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<ProvisionedMeshNode> nodesList = new ArrayList<ProvisionedMeshNode>();
    private Context context;
    private ProvisionedNodesViewmodes view;
    public static String TAG = ProvisionedNodesAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;

    // data is passed into the constructor
    public ProvisionedNodesAdapter(AppCompatActivity context, ProvisionedNodesViewmodes view) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.view = view;

        view.getNodes().observe(context, new Observer<List<ProvisionedMeshNode>>() {
            @Override
            public void onChanged(@Nullable List<ProvisionedMeshNode> nodes) {
                nodesList = new ArrayList<>(nodes);
                Log.d(TAG, Integer.toString(nodesList.size()));
                notifyDataSetChanged();
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.device_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(nodesList.get(position).getUuid());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return nodesList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        ToggleButton myToggleButton;
        RelativeLayout itemBox;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.device_name);
            itemBox = itemView.findViewById(R.id.item_box);
            itemBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProvisionedMeshNode node = nodesList.get(getAdapterPosition());
                    onItemClickListener.onItemClick(node);
                }
            });
        }
    }

    // convenience method for getting data at click position
    ProvisionedMeshNode getItem(int id) {
        return nodesList.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface OnItemClickListener {
        void onItemClick(ProvisionedMeshNode node);
    }
}
