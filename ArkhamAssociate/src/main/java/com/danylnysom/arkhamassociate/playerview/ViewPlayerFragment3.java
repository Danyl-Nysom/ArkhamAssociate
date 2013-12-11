package com.danylnysom.arkhamassociate.playerview;

import android.database.Cursor;
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

        if (investigator != null) {
            String story = investigator.getString(investigator.getColumnIndex(DBHelper.COL_STORY));
            ((TextView) rootView.findViewById(R.id.story)).setText(story);
        }

        return rootView;
    }

    @Override
    public void update(Cursor player, Cursor investigator) {
        this.investigator = investigator;
    }
}