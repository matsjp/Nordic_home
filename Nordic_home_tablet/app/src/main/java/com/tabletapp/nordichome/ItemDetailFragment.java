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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments().containsKey("group")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            group = getArguments().getParcelable("group");
            //mItem = GroupContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_NAME));
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        //groupOfScenes.setVisibility(View.GONE);

        /*TODO: Her maa vi paa en eller annen maate klare aa ta bort knapper som gjelder scenes som ikke er i JSON filen
          btn.setVisiblity(View.GONE)
          btn.setVisiblity(Viev.VISIBLE)
        */


        //todo: num has to be the number of scenes connected the the group
        if (group != null) {
            //Setting the text for the detail part. This is where we want the gridview to show.
            ((TextView) rootView.findViewById(R.id.item_detail_text)).setText(group.getName());
            //Changing header when button is clicked - not done.

            RadioGroup groupOfScenes = rootView.findViewById(R.id.scenebuttons);
            ApplicationExtension application = (ApplicationExtension) getContext().getApplicationContext();
            ArrayList<Scene> scenes = new ArrayList<>(application.getScannerRepo().getGroupsScenes(group));
            //ArrayList<SceneItem> scenesInGroup = mItem.getScenesList();
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
                //radioButton.setBackground(getContext().getResources().getDrawable(R.drawable.button_background));
                radioButton.setBackgroundResource(R.drawable.button_background);
                radioButton.setGravity(Gravity.CENTER);
                radioButton.setButtonDrawable(android.R.color.transparent);
                radioButton.setText(scene.getName());
                radioButton.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                        300,
                        300
                );
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
