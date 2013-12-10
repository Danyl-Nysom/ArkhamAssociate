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

class ViewPlayerFragment1 extends Fragment {
    private final Cursor investigator;

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

        ((TextView) rootView.findViewById(R.id.investigatorName)).setText(name);
        ((TextView) rootView.findViewById(R.id.home)).setText(home);
        ((TextView) rootView.findViewById(R.id.ability)).setText(ability);
        ((TextView) rootView.findViewById(R.id.posFixed)).setText(posFixed);
        ((TextView) rootView.findViewById(R.id.posRandom)).setText(posRandom);

        return rootView;
    }
}
