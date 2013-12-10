package com.danylnysom.arkhamassociate.playerselect;

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
import com.danylnysom.arkhamassociate.db.PlayerStats;
import com.danylnysom.arkhamassociate.playerview.ViewPlayerActivity;

import java.util.Random;

class PlayerClickListener implements View.OnClickListener, PlayerStats {
    private final int key;
    private final Uri playersUri;
    private static final String[] PROJECTION = {DBHelper.COL_NAME, DBHelper.COL_STATS};
    private Context context;
    private final boolean hasInvestigator;

    private int selectedIndex = 1;
    private Cursor cursor;

    public PlayerClickListener(int key, Uri playersUri, boolean hasInvestigator) {
        this.key = key;
        this.playersUri = playersUri;
        this.hasInvestigator = hasInvestigator;
    }

    @Override
    public void onClick(View v) {
        context = v.getContext();
        if (hasInvestigator) {
            openPlayerViewer();
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
                    openPlayerViewer();
                }
            });

            dialog.setNeutralButton("Randomize", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedIndex = new Random().nextInt(new DBHelper(context).getInvestigatorCount());
                    addInvestigatorToPlayer();
                    openPlayerViewer();
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
        int stats = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_STATS));
        int sneak = (stats >> SNEAK_SHIFT) & STAT_MASK;
        int will = (stats >> WILL_SHIFT) & STAT_MASK;
        int luck = (stats >> LUCK_SHIFT) & STAT_MASK;

        stats ^= (sneak << SNEAK_SHIFT) + (will << WILL_SHIFT) + (luck << LUCK_SHIFT);
        stats |= (sneak - 3 << SNEAK_SHIFT) + (will - 3 << WILL_SHIFT) + (luck - 3 << LUCK_SHIFT);

        values.put(DBHelper.COL_INVESTIGATOR, investigatorName);
        values.put(DBHelper.COL_STATS, stats);
        values.put(DBHelper.COL_CLUES, cursor.getInt(cursor.getColumnIndex(DBHelper.COL_CLUES)));
        values.put(DBHelper.COL_MONEY, cursor.getInt(cursor.getColumnIndex(DBHelper.COL_MONEY)));
        values.put(DBHelper.COL_BLESSED, cursor.getInt(cursor.getColumnIndex(DBHelper.COL_BLESSED)));

        resolver.update(ContentUris.withAppendedId(playersUri, key), values, null, null);
        cursor.close();
    }

    private void openPlayerViewer() {
        Intent intent = new Intent(context, ViewPlayerActivity.class);
        intent.putExtra(ViewPlayerActivity.ARG_KEY, key);
        intent.putExtra(ViewPlayerActivity.ARG_URI, playersUri.toString());
        context.startActivity(intent);
    }
}
