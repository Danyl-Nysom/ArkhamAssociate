package com.danylnysom.arkhamassociate;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Dylan on 17/11/13.
 */
public class PlayerClickListener implements View.OnClickListener {
    private int key;
    private Uri playersUri;
    private static final String[] PROJECTION = {DBHelper.COL_NAME, DBHelper.COL_STATS};
    private Context context;
    boolean hasInvestigator;
    private String playerName;

    private int selectedIndex = 1;
    private Cursor cursor;

    public PlayerClickListener(int key, String playerName, Uri playersUri, boolean hasInvestigator) {
        this.key = key;
        this.playersUri = playersUri;
        this.hasInvestigator = hasInvestigator;
        this.playerName = playerName;
    }

    @Override
    public void onClick(View v) {
        context = v.getContext();
        if (hasInvestigator) {
            Intent intent = new Intent(context, ViewPlayerActivity.class);
            intent.putExtra(ViewPlayerActivity.ARG_KEY, key);
            intent.putExtra(ViewPlayerActivity.ARG_URI, playersUri.toString());
            context.startActivity(intent);
        } else {
            cursor = context.getContentResolver().query(ArkhamProvider.INVESTIGATORS_URI, PROJECTION,
                    null, null, DBHelper.COL_KEY);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Select an Investigator");

            selectedIndex = 0;
            dialog.setSingleChoiceItems(cursor, selectedIndex, DBHelper.COL_NAME, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedIndex = which;
                }
            });

            dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addInvestigatorToPlayer();
                }
            });

            dialog.setNeutralButton("Randomize", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedIndex = new Random().nextInt(new DBHelper(context).getInvestigatorCount());
                    addInvestigatorToPlayer();
                }
            });

            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            dialog.show();
        }
    }

    private void addInvestigatorToPlayer() {
        ContentResolver resolver = context.getContentResolver();
        cursor.moveToPosition(selectedIndex);

        ContentValues values = new ContentValues();
        String investigatorName = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME));
        values.put(DBHelper.COL_INVESTIGATOR, investigatorName);
        values.put(DBHelper.COL_STATS, cursor.getInt(cursor.getColumnIndex(DBHelper.COL_STATS)));

        Set<Map.Entry<String, Object>> contents = values.valueSet();

        resolver.update(ContentUris.withAppendedId(playersUri, key), values, null, null);
        cursor.close();
        hasInvestigator = true;
    }
}
