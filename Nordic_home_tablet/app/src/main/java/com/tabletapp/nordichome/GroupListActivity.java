package com.tabletapp.nordichome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.MeshNetwork;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ScenesActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class GroupListActivity extends AppCompatActivity {

    private static final String TAG = GroupListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //Landscape mode

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        //TextView txtAddress = (TextView) findViewById(R.id.txtAddress);
        //txtAddress.setText(currentNW.getName());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        /*RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.Adapter adapter = new ItemListAdapter(this);
        recyclerView.setAdapter(adapter);

        ApplicationExtension application = (ApplicationExtension) getApplication();
        MeshNetwork meshNetwork = application.getScannerRepo().getMeshManagerApi().getMeshNetwork();

        //Log.d(TAG, meshNetwork.getMeshUUID());
        if (meshNetwork != null) {
            for (Group group : meshNetwork.getGroups()) {
                ((ItemListAdapter) adapter).addData(group);
            }
        }*/


        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show sign up activity
            startActivity(new Intent(GroupListActivity.this, StartPageActivity.class));
            Toast.makeText(GroupListActivity.this, "Run only once", Toast.LENGTH_LONG)
                    .show();
        }


        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        ApplicationExtension application = (ApplicationExtension) getApplication();
        MeshNetwork meshNetwork = application.getScannerRepo().getMeshManagerApi().getMeshNetwork();

        //Log.d(TAG, meshNetwork.getMeshUUID());
        ArrayList<Group> groups = new ArrayList<>();
        if (meshNetwork != null) {
            groups = new ArrayList<>(meshNetwork.getGroups());
        }
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, groups, mTwoPane));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu and update JSON actions in the actionbar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG, "Import button");
                Intent intent = new Intent(GroupListActivity.this, NetworkActivity.class);
                startActivity(intent); break;
            case R.id.connect:
                Log.d(TAG, "ConnectButton");
                ApplicationExtension application = (ApplicationExtension) getApplication();
                application.getScannerRepo().connectToProvisionedNode(); break;
            case R.id.signOut:
                Log.d(TAG, Integer.toString(item.getItemId()));
                signOut(); break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item);

        }
        return false;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final GroupListActivity mParentActivity;
        private final List<Group> groups;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group group = (Group) view.getTag();

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("group", group);
                    GroupScenesFragment fragment = new GroupScenesFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ScenesActivity.class);
                    intent.putExtra("group", group);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(GroupListActivity parent,
                                      List<Group> groups,
                                      boolean twoPane) {
            this.groups = groups;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentView.setText(groups.get(position).getName());
            holder.itemView.setTag(groups.get(position));

            //holder.itemView.setTag(groups.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }

    private void signOut(){
        Log.d(TAG, "Signing out");
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        client.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(GroupListActivity.this, StartPageActivity.class);
                startActivity(intent);
                Log.d(TAG, "Signed out");
            }
        });

    }
}
