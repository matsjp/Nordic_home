package com.tabletapp.nordichome;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tabletapp.nordichome.data.GroupContent;
import com.tabletapp.nordichome.data.GroupItem;
import com.tabletapp.nordichome.data.SceneItem;

import java.util.ArrayList;

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


    /**
     * The dummy content this fragment is presenting.
     */
    private GroupItem mItem;
    private ArrayList<SceneItem> groupScenes;
    private int currentNum;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = GroupContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_NAME));
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        RadioGroup groupOfScenes = rootView.findViewById(R.id.btnScenes);
        //groupOfScenes.setVisibility(View.GONE);

        /*TODO: Her maa vi paa en eller annen maate klare aa ta bort knapper som gjelder scenes som ikke er i JSON filen
          btn.setVisiblity(View.GONE)
          btn.setVisiblity(Viev.VISIBLE)
        */


        //todo: num has to be the number of scenes connected the the group
        if (mItem != null) {
            //Setting the text for the detail part. This is where we want the gridview to show.
            ((TextView) rootView.findViewById(R.id.item_detail_text)).setText(mItem.name);
            //Changing header when button is clicked - not done.
            groupOfScenes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    switch(checkedId)
                    {
                        case R.id.btnOn:
                            ((TextView) rootView.findViewById(R.id.item_detail_text)).setText("hallooo");
                            break;
                        case R.id.btnOff:
                            ((TextView) rootView.findViewById(R.id.item_detail_text)).setText("hallooo2");
                            break;
                        case R.id.btnDim:
                            ((TextView) rootView.findViewById(R.id.item_detail_text)).setText("hallooo3");
                            break;
                    }
                }
            });
            // test adding a radio button programmatically

           /* LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);

            RadioButton newRadioButton = new RadioButton(this.getContext());
            newRadioButton.setText(mItem.getScenesList().get(0).getName());
            newRadioButton.setId(R.id.btnOn);
            groupOfScenes.addView(newRadioButton, 0, layoutParams);

            RadioButton newRadioButton2 = new RadioButton(this.getContext());
            newRadioButton2.setText(mItem.getScenesList().get(1).getName());
            newRadioButton2.setId(R.id.btnOff);
            groupOfScenes.addView(newRadioButton2, 1, layoutParams);

            RadioButton newRadioButton3 = new RadioButton(this.getContext());
            newRadioButton3.setText(mItem.getScenesList().get(1).getName());
            newRadioButton3.setId(R.id.btnDim);
            groupOfScenes.addView(newRadioButton3, 3, layoutParams);*/

        }


        return rootView;
    }
}
