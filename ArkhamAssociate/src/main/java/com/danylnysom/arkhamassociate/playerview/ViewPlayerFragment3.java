package com.danylnysom.arkhamassociate.playerview;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danylnysom.arkhamassociate.R;
import com.danylnysom.arkhamassociate.db.DBHelper;

public class ViewPlayerFragment3 extends Fragment implements ViewPlayerFragment {
    private Cursor investigator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = getView();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_player_3, null);
        }

        Typeface face = Typeface.createFromAsset(rootView.getContext().getAssets(),
                "typeface.ttf");

        TextView storyLabel = (TextView) rootView.findViewById(R.id.storyLabel);
        storyLabel.setTypeface(face);

        if (investigator != null) {
            String story = investigator.getString(investigator.getColumnIndex(DBHelper.COL_STORY));
            TextView storyView = (TextView) rootView.findViewById(R.id.story);
            storyView.setText(story);
            storyView.setTypeface(face);
        }

        return rootView;
    }

    @Override
    public void update(Cursor player, Cursor investigator) {
        this.investigator = investigator;
    }
}