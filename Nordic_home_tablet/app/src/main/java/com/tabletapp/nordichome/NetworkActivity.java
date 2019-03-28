package com.tabletapp.nordichome;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.util.Collections;

public class NetworkActivity extends AppCompatActivity {

    private static final String TAG = NetworkActivity.class.getSimpleName();
    private DriveServiceRepo driveRepo;
    private JsonFilesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.json_files_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final JsonFilesViewModel view = ViewModelProviders.of(this).get(JsonFilesViewModel.class);
        view.setApplication((ApplicationExtension) getApplication());

        // specify an adapter (see also next example)
        mAdapter = new JsonFilesAdapter(this, view);
        recyclerView.setAdapter(mAdapter);

        view.getImportFailSignal().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean signal) {
                Log.d(TAG, "getImportFailSignal");
                if (signal){
                    Log.d(TAG, "import failed");
                    ApplicationExtension application = (ApplicationExtension) getApplication();
                    application.getScannerRepo().getImportFailSignal().postValue(false);
                }
            }
        });
        ApplicationExtension application = (ApplicationExtension) getApplication();
        driveRepo = application.getDriveServiceRepo();
        // Authenticate the user. For most apps, this should be done when the user performs an
        // action that requires Drive access rather than in onCreate.
        if (driveRepo == null){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null){
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                        this, Collections.singleton(DriveScopes.DRIVE));
                credential.setSelectedAccount(account.getAccount());
                Drive googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("Nordic Home")
                        .build();
                driveRepo = new DriveServiceRepo(googleDriveService, this);
                application.setDriveServiceRepo(driveRepo);
            }

        }
        query();
    }

    private void query() {
        if (driveRepo != null) {
            Log.d(TAG, "Querying for files.");

            driveRepo.queryJsonFiles()
                    .addOnSuccessListener(fileList -> {
                        for (File file : fileList.getFiles()) {
                            ((JsonFilesAdapter) mAdapter).addData(file);
                        }
                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
        else{
            Log.d(TAG, "driveRepo is null");
        }
    }
}
