package com.danylnysom.arkhamassociate.playerview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.danylnysom.arkhamassociate.R;
import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;

class ChangeValueFragment extends DialogFragment {
    public static final String VIEW_ID_ARG = "view_id";
    public static final String PLAYER_KEY_ARG = "player";
    public static final String GAME_KEY_ARG = "game";

    public static final String MONEY_ARG = "money";

    private ContentValues values;
    private String title;

    private void createMoneyView(LinearLayout rootView) {
        title = "Money";

        NumberPicker money = new NumberPicker(rootView.getContext());
        money.setMinValue(0);
        money.setMaxValue(100);
        money.setValue(getArguments().getInt(MONEY_ARG));
        money.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                values.put(DBHelper.COL_MONEY, newVal);
            }
        });
        rootView.addView(money);
    }

    private void setValues() {
        ContentResolver resolver = getActivity().getContentResolver();
        Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                .appendPath("" + getArguments().getInt(GAME_KEY_ARG))
                .appendPath("players").appendPath("" + getArguments().getInt(PLAYER_KEY_ARG))
                .build();
        resolver.update(uri, values, null, null);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout rootView = new LinearLayout(getActivity());
        values = new ContentValues();

        switch (getArguments().getInt(VIEW_ID_ARG)) {
            case R.id.money:
                createMoneyView(rootView);
                break;
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setValues();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                )
                .setView(rootView)
                .create();
    }
}
