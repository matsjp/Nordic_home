package com.tabletapp.nordichome;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.tabletapp.nordichome.data.GroupContent;
import com.tabletapp.nordichome.data.GroupItem;

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

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the group content as text in a TextView.
        if (mItem != null) {
            //Setting the text for the detail part. This is where we want the gridview to show.
            //TODO: Finne ut hvordan gridview skal kalles her, og vises her.
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.name);
        }

        return rootView;
    }
}
