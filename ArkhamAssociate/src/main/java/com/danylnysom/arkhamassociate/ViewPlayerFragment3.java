package com.danylnysom.arkhamassociate;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danylnysom.arkhamassociate.db.DBHelper;

/**
 * Created by Dylan on 18/11/13.
 */
public class ViewPlayerFragment3 extends Fragment {
    private Cursor investigator;

    public ViewPlayerFragment3(Cursor investigator) {
        this.investigator = investigator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_3, null);

        String story = investigator.getString(investigator.getColumnIndex(DBHelper.COL_STORY));

        ((TextView) rootView.findViewById(R.id.story)).setText(story);

        return rootView;
    }
}