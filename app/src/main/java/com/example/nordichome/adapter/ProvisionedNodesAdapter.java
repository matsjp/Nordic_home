package com.example.nordichome.adapter;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nordichome.MainActivity;
import com.example.nordichome.R;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import viewmodels.ProvisionedNodesViewmodes;

public class ProvisionedNodesAdapter extends RecyclerView.Adapter<ProvisionedNodesAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ArrayList<ProvisionedMeshNode> nodesList = new ArrayList<ProvisionedMeshNode>();
    private Context context;
    private ProvisionedNodesViewmodes view;
    public static String TAG = ProvisionedMeshNode.class.getSimpleName();

    // data is passed into the constructor
    public ProvisionedNodesAdapter(MainActivity context, ProvisionedNodesViewmodes view) {
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.device_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ProvisionedMeshNode getItem(int id) {
        return nodesList.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
