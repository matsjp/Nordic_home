package com.tabletapp.nordichome;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.drive.model.File;

import java.util.ArrayList;

public class JsonFilesAdapter extends RecyclerView.Adapter<JsonFilesAdapter.MyViewHolder> {
    private ArrayList<File> mDataset = new ArrayList<>();
    public static String TAG = ItemListAdapter.class.getSimpleName();
    private Context context;
    private JsonFilesViewModel jsonFilesViewModel;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        RelativeLayout layout;

        public MyViewHolder(View v) {
            super(v);
            layout = v.findViewById(R.id.json_item_box);
            textView = v.findViewById(R.id.json_textView);

            layout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Log.d(ItemListAdapter.TAG, mDataset.get(getAdapterPosition()).getId());
                    ApplicationExtension application = (ApplicationExtension) context.getApplicationContext();
                    application.getDriveServiceRepo().downloadFile(mDataset.get(getAdapterPosition()).getId()).addOnSuccessListener(name -> {
                        java.io.File file = new java.io.File(context.getFilesDir(), name);
                        Uri uri = Uri.fromFile(file);
                        application.getScannerRepo().getBleMeshManager().disconnect();
                        application.getScannerRepo().getMeshManagerApi().importMeshNetwork(uri);
                    }).addOnFailureListener(exception -> Log.e(TAG, "Unable to download file.", exception));;
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public JsonFilesAdapter(AppCompatActivity context, JsonFilesViewModel jsonFilesViewModel) {
        this.context = context;
        this.jsonFilesViewModel = jsonFilesViewModel;

        jsonFilesViewModel.getImportSuccessSignal().observe(context, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean signal) {
                Log.d(TAG, "getImportSuccessSignal");
                if (signal){
                    ApplicationExtension application = (ApplicationExtension) context.getApplication();
                    application.getScannerRepo().getImportSuccessSignal().postValue(false);
                    Toast.makeText(context, "Network Imported", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, ItemListActivity.class);
                    context.startActivity(intent);

                }
            }
        });
    }

    public void addData(File data){
        mDataset.add(data);
        notifyItemInserted(mDataset.size() - 1);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JsonFilesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.json_text_view, parent, false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).getName().substring(0, mDataset.get(position).getName().length() - 5));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
