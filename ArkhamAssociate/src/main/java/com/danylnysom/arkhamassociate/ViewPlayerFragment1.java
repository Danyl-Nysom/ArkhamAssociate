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
public class ViewPlayerFragment1 extends Fragment {
    private Cursor investigator;

    public ViewPlayerFragment1(Cursor investigator) {
        this.investigator = investigator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_1, null);

        String name = investigator.getString(investigator.getColumnIndex(DBHelper.COL_NAME));
        String home = investigator.getString(investigator.getColumnIndex(DBHelper.COL_HOME));
        String ability = investigator.getString(investigator.getColumnIndex(DBHelper.COL_ABILITY));
        String posFixed = investigator.getString(investigator.getColumnIndex(DBHelper.COL_POSSESSIONS_FIXED))
                .replace("|", "\n");
        String posRandom = investigator.getString(investigator.getColumnIndex(DBHelper.COL_POSSESSIONS_RANDOM))
                .replace("|", "\n");
        String story = investigator.getString(investigator.getColumnIndex(DBHelper.COL_ABILITY));

        ((TextView) rootView.findViewById(R.id.investigatorName)).setText(name);
        ((TextView) rootView.findViewById(R.id.home)).setText(home);
        ((TextView) rootView.findViewById(R.id.ability)).setText(ability);
        ((TextView) rootView.findViewById(R.id.posFixed)).setText(posFixed);
        ((TextView) rootView.findViewById(R.id.posRandom)).setText(posRandom);
        ((TextView) rootView.findViewById(R.id.story)).setText(story);

        return rootView;
    }
}