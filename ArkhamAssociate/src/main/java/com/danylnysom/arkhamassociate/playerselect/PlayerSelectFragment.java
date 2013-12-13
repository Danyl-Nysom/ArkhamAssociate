package com.danylnysom.arkhamassociate.playerselect;

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

import com.danylnysom.arkhamassociate.R;
import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;

/**
 * A fragment displaying a list of players, allowing the user to select one. Upon selection, a
 * {@link com.danylnysom.arkhamassociate.playerview.ViewPlayerActivity} will be started. New
 * players can also be added through the ActionBar.
 */
public class PlayerSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private Context context = null;
    private Uri playersUri = null;

    private PlayerSelectAdapter mAdapter;

    private int gameKey;
    private String gameName;

    static public PlayerSelectFragment newInstance(int gameKey, String gameName) {
        PlayerSelectFragment fragment = new PlayerSelectFragment();
        fragment.gameKey = gameKey;
        fragment.gameName = gameName;
        fragment.playersUri = Uri.withAppendedPath(ContentUris.withAppendedId(ArkhamProvider.GAMES_URI, gameKey), "players");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_game_select, container, false);
        if (rootView != null) {
            context = container.getContext();
            mAdapter = new PlayerSelectAdapter(context, null, 0);
            rootView.setAdapter(mAdapter);
        }
        LoaderManager.LoaderCallbacks<Cursor> mCallbacks = this;
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
        switch (id) {
            case R.id.action_add:
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
                break;
            case R.id.action_remove:
                ContentResolver resolver = getActivity().getContentResolver();
                int playerCount = resolver.delete(playersUri, null, null);
                System.err.println("Deleted " + playerCount + " players");
                int gameCount = resolver.delete(ContentUris.withAppendedId(ArkhamProvider.GAMES_URI, gameKey),
                        null, null);
                System.err.println("Deleted " + gameCount + " games");
                getActivity().getFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LinearLayout itemView = new LinearLayout(context);
            itemView.setOrientation(LinearLayout.VERTICAL);
            itemView.setMinimumHeight(200);

            TextView mainText = new TextView(context);
            mainText.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
            itemView.addView(mainText, 0);

            TextView subText = new TextView(context);
            subText.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Small);
            itemView.addView(subText, 1);

            bindView(itemView, context, cursor);

            return itemView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String playerName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME));
            String investigatorName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_INVESTIGATOR));
            LinearLayout itemView = (LinearLayout) view;
            TextView mainText = (TextView) itemView.getChildAt(0);
            TextView subText = (TextView) itemView.getChildAt(1);

            mainText.setText(playerName);
            subText.setText(investigatorName);

            itemView.setOnClickListener(new PlayerClickListener(
                    cursor.getInt(cursor.getColumnIndex(DBHelper.COL_KEY)), playersUri,
                    cursor.getString(cursor.getColumnIndex(DBHelper.COL_INVESTIGATOR)) != null));
        }
    }
}