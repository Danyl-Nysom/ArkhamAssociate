package com.danylnysom.arkhamassociate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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

/**
 * Created by Dylan on 15/11/13.
 */
public class PlayerSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private ListView rootView = null;
    private Context context = null;
    private Uri playersUri = null;

    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private PlayerSelectAdapter mAdapter;

    private int gameKey;
    private String gameName;

    public PlayerSelectFragment(int gameKey, String gameName) {
        this.gameKey = gameKey;
        this.gameName = gameName;
        playersUri = Uri.withAppendedPath(ContentUris.withAppendedId(ArkhamProvider.GAMES_URI, gameKey), "players");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ListView) inflater.inflate(R.layout.fragment_game_select, container, false);
        if (rootView != null) {
            context = container.getContext();
            mAdapter = new PlayerSelectAdapter(context, null, 0);
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
        inflater.inflate(R.menu.arkham_player_select, menu);
        getActivity().getActionBar().setTitle(gameName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            final EditText input = new EditText(context);
            input.setHint("Bob");

            dialog.setTitle("Enter the players' name");
            dialog.setView(input);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (input.length() > 0) {
                        ContentResolver resolver = getActivity().getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.COL_NAME, input.getText().toString());
                        values.put(DBHelper.COL_GAME, gameKey);
                        resolver.insert(playersUri, values);
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), playersUri, null, null, null, null);
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

    private class PlayerSelectAdapter extends CursorAdapter {

        public PlayerSelectAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(final Context context, Cursor cursor, ViewGroup parent) {
            String playerName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME));
            String investigatorName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_INVESTIGATOR));
            LinearLayout itemView = new LinearLayout(context);
            TextView mainText = new TextView(context);
            TextView subText = new TextView(context);

            mainText.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
            mainText.setText(playerName);
            itemView.addView(mainText, 0);

            subText.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Small);
            subText.setText(investigatorName);
            itemView.addView(subText, 1);

            itemView.setOrientation(LinearLayout.VERTICAL);
            itemView.setMinimumHeight(200);


            itemView.setOnClickListener(new PlayerClickListener(
                    cursor.getInt(cursor.getColumnIndex(DBHelper.COL_KEY)), playerName, playersUri,
                    cursor.getString(cursor.getColumnIndex(DBHelper.COL_INVESTIGATOR)) != null));

            return itemView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            LinearLayout itemView = (LinearLayout) view;
            TextView mainText = (TextView) itemView.getChildAt(0);
            TextView subText = (TextView) itemView.getChildAt(1);

            mainText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME)));
            subText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INVESTIGATOR)));
        }
    }
}