package com.tabletapp.nordichome.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter that works as a middleman between GridView and Scene class
 * @author Sunniva Mathea Runde
 */

public class ScenesAdapter extends BaseAdapter {

    private final Context mContext;
    private final SceneItem[] scenes;

    public ScenesAdapter(Context context, SceneItem[] scenes) {
        this.mContext = context;
        this.scenes = scenes;
    }


    @Override
    public int getCount() {
        return scenes.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(String.valueOf(position));
        return dummyTextView;
    }

}
