package com.tabletapp.nordichome;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.Scene;
import no.nordicsemi.android.meshprovisioner.transport.SceneRecall;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item NAME that this fragment
     * represents.
     */
    public static final String ARG_ITEM_NAME = "item_name";
    public static final String TAG = ItemDetailFragment.class.getSimpleName();
    private Group group;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments().containsKey("group")) {
            group = getArguments().getParcelable("group");
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        if (group != null) {
            ((TextView) rootView.findViewById(R.id.item_detail_text)).setText(group.getName());
            RadioGroup groupOfScenes = rootView.findViewById(R.id.scenebuttons);
            ApplicationExtension application = (ApplicationExtension) getContext().getApplicationContext();
            ArrayList<Scene> scenes = new ArrayList<>(application.getScannerRepo().getGroupsScenes(group));
            groupOfScenes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    byte[] appKey = application.getScannerRepo().getMeshManagerApi().getMeshNetwork().getAppKeys().get(0).getKey();
                    SceneRecall sceneRecall = new SceneRecall(appKey, scenes.get(checkedId).getNumber(), 500);
                    application.getScannerRepo().getMeshManagerApi().sendMeshMessage(group.getGroupAddress(), sceneRecall);
                }
            });

            int index = 0;
            for (Scene scene : scenes) {
                RadioButton radioButton = new RadioButton(this.getContext());
                radioButton.setId(index);
                radioButton.setBackgroundResource(R.drawable.button_background);
                radioButton.setGravity(Gravity.CENTER);
                radioButton.setButtonDrawable(android.R.color.transparent);
                radioButton.setText(scene.getName());
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(300, 300);
                params.setMargins(10, 10, 10, 10);
                radioButton.setPadding(50,50,50,50);
                radioButton.setTextSize(25);
                radioButton.setLayoutParams(params);
                groupOfScenes.addView(radioButton);
                index++;
            }

        }
        return rootView;
    }
}
