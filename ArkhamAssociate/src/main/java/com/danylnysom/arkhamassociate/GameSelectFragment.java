package com.danylnysom.arkhamassociate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
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
import android.widget.Toast;

import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;

import java.util.Date;

/**
 * Created by Dylan on 15/11/13.
 */
public class GameSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context context = null;
    private ListView rootView = null;

    private static final int LOADER_ID = 1;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private GameSelectAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ListView) inflater.inflate(R.layout.fragment_game_select, container, false);
        if (rootView != null) {
            context = container.getContext();
            mAdapter = new GameSelectAdapter(context, null, 0);
            rootView.setAdapter(mAdapter);
        }
        mCallbacks = this;
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
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
                    if (input.length() > 0) {
                        ContentResolver resolver = getActivity().getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.COL_NAME, input.getText().toString());
                        values.put(DBHelper.COL_CREATION, new Date().getTime());
                        resolver.insert(ArkhamProvider.GAMES_URI, values);
                    } else {
                        Toast.makeText(context, "Name cannot be blank!", Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ArkhamProvider.GAMES_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
                mAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class GameSelectAdapter extends CursorAdapter {

        public GameSelectAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final int gameId = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_KEY));
            final String gameName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME));
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
                    ft.replace(R.id.container, new PlayerSelectFragment(gameId, gameName));
                    ft.commit();
                }
            });

            return itemView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final int gameId = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_KEY));
            final String gameName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME));
            LinearLayout itemView = (LinearLayout) view;
            TextView mainText = (TextView) itemView.getChildAt(0);
            TextView subText = (TextView) itemView.getChildAt(1);

            mainText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME)));
            subText.setText(new Date(cursor.getLong(cursor.getColumnIndex(DBHelper.COL_CREATION))).toString());

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container, new PlayerSelectFragment(gameId, gameName));
                    ft.commit();
                }
            });
        }
    }
}