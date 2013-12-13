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
import android.widget.TextView;

import com.danylnysom.arkhamassociate.R;
import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;

class ChangeValueFragment extends DialogFragment {
    public static final String VIEW_ID_ARG = "view_id";
    public static final String PLAYER_KEY_ARG = "player";
    public static final String GAME_KEY_ARG = "game";

    public static final String MONEY_ARG = "money";
    public static final String CLUES_ARG = "clues";
    public static final String SANITY_ARG = "sanity";
    public static final String MAX_SANITY_ARG = "max_sanity";
    public static final String STAMINA_ARG = "stamina";
    public static final String MAX_STAMINA_ARG = "max_stamina";

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

    private void createCluesView(LinearLayout rootView) {
        title = "Clues";

        NumberPicker clues = new NumberPicker(rootView.getContext());
        clues.setMinValue(0);
        clues.setMaxValue(100);
        clues.setValue(getArguments().getInt(CLUES_ARG));
        clues.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                values.put(DBHelper.COL_CLUES, newVal);
            }
        });
        rootView.addView(clues);
    }

    private void createSanityView(LinearLayout rootView) {
        title = "Sanity";

        final NumberPicker current = new NumberPicker(rootView.getContext());
        current.setMinValue(0);
        current.setMaxValue(10);
        current.setValue(getArguments().getInt(SANITY_ARG));

        final NumberPicker max = new NumberPicker(rootView.getContext());
        max.setMinValue(0);
        max.setMaxValue(10);
        max.setValue(getArguments().getInt(MAX_SANITY_ARG));

        current.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                values.put(DBHelper.COL_SANITY, newVal);
                if (newVal > max.getValue()) {
                    max.setValue(newVal);
                    values.put(DBHelper.COL_SANITY_MAX, newVal);
                }
            }
        });
        max.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                values.put(DBHelper.COL_SANITY_MAX, newVal);
                if (newVal < current.getValue()) {
                    current.setValue(newVal);
                    values.put(DBHelper.COL_SANITY, newVal);
                }
            }
        });

        TextView currentLabel = new TextView(rootView.getContext());
        currentLabel.setText("Current: ");
        TextView maxLabel = new TextView(rootView.getContext());
        maxLabel.setText("Max: ");

        rootView.addView(currentLabel);
        rootView.addView(current);
        rootView.addView(maxLabel);
        rootView.addView(max);
    }

    private void createStaminaView(LinearLayout rootView) {
        title = "Stamina";

        final NumberPicker current = new NumberPicker(rootView.getContext());
        current.setMinValue(0);
        current.setMaxValue(10);
        current.setValue(getArguments().getInt(STAMINA_ARG));

        final NumberPicker max = new NumberPicker(rootView.getContext());
        max.setMinValue(0);
        max.setMaxValue(10);
        max.setValue(getArguments().getInt(MAX_STAMINA_ARG));

        current.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                values.put(DBHelper.COL_STAMINA, newVal);
                if (newVal > max.getValue()) {
                    max.setValue(newVal);
                    values.put(DBHelper.COL_STAMINA_MAX, newVal);
                }
            }
        });
        max.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                values.put(DBHelper.COL_STAMINA_MAX, newVal);
                if (newVal < current.getValue()) {
                    current.setValue(newVal);
                    values.put(DBHelper.COL_STAMINA, newVal);
                }
            }
        });

        TextView slash = new TextView(rootView.getContext());
        slash.setText("/");

        rootView.addView(current);
        rootView.addView(slash);
        rootView.addView(max);
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
            case R.id.clues:
                createCluesView(rootView);
                break;
            case R.id.stamina:
                createStaminaView(rootView);
                break;
            case R.id.sanity:
                createSanityView(rootView);
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
