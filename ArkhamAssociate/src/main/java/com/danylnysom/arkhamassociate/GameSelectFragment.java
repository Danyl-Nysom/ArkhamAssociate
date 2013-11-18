package com.danylnysom.arkhamassociate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.danylnysom.arkhamassociate.db.DBHelper;

import java.util.Date;

/**
 * Created by Dylan on 15/11/13.
 */
public class GameSelectFragment extends Fragment {
    private Context context = null;
    private Activity activity = null;
    private ListView rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ListView) inflater.inflate(R.layout.fragment_game_select, container, false);
        if (rootView != null) {
            context = container.getContext();
            rootView.setAdapter(new GameSelectAdapter(
                    context,
                    new DBHelper(context).getGames(),
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
            );
        }
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.arkham_game_select, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            final EditText input = new EditText(context);
            input.setHint("Bob & I fight Cthulhu");

            dialog.setTitle("Enter a name for the game");
            dialog.setView(input);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DBHelper(context).addGame(input.getText().toString(), new Date().getTime());
                    ((CursorAdapter) rootView.getAdapter()).changeCursor(new DBHelper(context).getGames());
                    ((CursorAdapter) rootView.getAdapter()).notifyDataSetChanged();
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ArkhamMainActivity) activity).onSectionAttached(ArkhamMainActivity.SECTION_PLAY);
    }

    private class GameSelectAdapter extends CursorAdapter {

        public GameSelectAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, final Cursor cursor, ViewGroup parent) {
            LinearLayout itemView = new LinearLayout(context);
            TextView mainText = new TextView(context);
            TextView subText = new TextView(context);

            mainText.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
            mainText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME)));
            itemView.addView(mainText, 0);

            subText.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Small);
            subText.setText(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COL_CREATION))).toString());
            itemView.addView(subText, 1);

            itemView.setOrientation(LinearLayout.VERTICAL);
            itemView.setMinimumHeight(200);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container, new PlayerSelectFragment(
                            cursor.getInt(cursor.getColumnIndex(DBHelper.COL_KEY))));
                    ft.commit();
                }
            });

            return itemView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }
}