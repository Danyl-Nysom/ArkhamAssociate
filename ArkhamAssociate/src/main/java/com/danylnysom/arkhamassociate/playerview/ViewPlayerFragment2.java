package com.danylnysom.arkhamassociate.playerview;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.danylnysom.arkhamassociate.R;
import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;
import com.danylnysom.arkhamassociate.db.PlayerStats;

class ViewPlayerFragment2 extends Fragment implements PlayerStats {
    private final Cursor investigator;
    private final Cursor player;

    private CheckBox blessed;
    private CheckBox cursed;
    private int gameKey;
    private int playerKey;

    public ViewPlayerFragment2(Cursor investigator, Cursor player) {
        this.investigator = investigator;
        this.player = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_2, null);
        int maxStats = investigator.getInt(investigator.getColumnIndex(DBHelper.COL_STATS));
        final int maxSpeed = maxStats >> SPEED_SHIFT & STAT_MASK;
        final int maxSneak = maxStats >> SNEAK_SHIFT & STAT_MASK;
        final int maxFight = maxStats >> FIGHT_SHIFT & STAT_MASK;
        final int maxWill = maxStats >> WILL_SHIFT & STAT_MASK;
        final int maxLore = maxStats >> LORE_SHIFT & STAT_MASK;
        final int maxLuck = maxStats >> LUCK_SHIFT & STAT_MASK;

        final int stats = player.getInt(player.getColumnIndex(DBHelper.COL_STATS));
        final int speed = stats >> SPEED_SHIFT & STAT_MASK;
        final int fight = stats >> FIGHT_SHIFT & STAT_MASK;
        final int lore = stats >> LORE_SHIFT & STAT_MASK;

        playerKey = player.getInt(player.getColumnIndex(DBHelper.COL_KEY));
        gameKey = player.getInt(player.getColumnIndex(DBHelper.COL_GAME));

        RadioGroup speedGroup = (RadioGroup) rootView.findViewById(R.id.speedGroup);
        ((RadioButton) (speedGroup.getChildAt(3 - maxSpeed + speed))).setChecked(true);
        speedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = 0;
                switch (checkedId) {
                    case R.id.speed1:
                        position = 0;
                        break;
                    case R.id.speed2:
                        position = 1;
                        break;
                    case R.id.speed3:
                        position = 2;
                        break;
                    case R.id.speed4:
                        position = 3;
                        break;
                }
                int newSpeed = maxSpeed - 3 + position;
                System.err.println("speed = " + newSpeed);
                int newStats = (stats ^ (speed << SPEED_SHIFT)) | (newSpeed << SPEED_SHIFT);
                ContentResolver resolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_STATS, newStats);
                Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                        .appendPath("" + gameKey).appendPath("players").appendPath("" + playerKey)
                        .build();
                resolver.update(uri, values, null, null);
            }
        });

        RadioGroup fightGroup = (RadioGroup) rootView.findViewById(R.id.fightGroup);
        ((RadioButton) (fightGroup.getChildAt(3 + fight - maxFight))).setChecked(true);
        fightGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = 0;
                switch (checkedId) {
                    case R.id.fight1:
                        position = 0;
                        break;
                    case R.id.fight2:
                        position = 1;
                        break;
                    case R.id.fight3:
                        position = 2;
                        break;
                    case R.id.fight4:
                        position = 3;
                        break;
                }
                int newFight = maxFight - 3 + position;
                int newStats = (stats ^ (fight << FIGHT_SHIFT)) | (newFight << FIGHT_SHIFT);
                ContentResolver resolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_STATS, newStats);
                Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                        .appendPath("" + gameKey).appendPath("players").appendPath("" + playerKey)
                        .build();
                resolver.update(uri, values, null, null);
            }
        });

        RadioGroup loreGroup = (RadioGroup) rootView.findViewById(R.id.loreGroup);
        ((RadioButton) (loreGroup.getChildAt(3 - maxLore + lore))).setChecked(true);
        loreGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = 0;
                switch (checkedId) {
                    case R.id.lore1:
                        position = 0;
                        break;
                    case R.id.lore2:
                        position = 1;
                        break;
                    case R.id.lore3:
                        position = 2;
                        break;
                    case R.id.lore4:
                        position = 3;
                        break;
                }
                int newLore = maxLore - 3 + position;
                int newStats = (stats ^ (lore << LORE_SHIFT)) | (newLore << LORE_SHIFT);
                ContentResolver resolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_STATS, newStats);
                Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                        .appendPath("" + gameKey).appendPath("players").appendPath("" + playerKey)
                        .build();
                resolver.update(uri, values, null, null);
            }
        });

        for (int i = 0; i < 4; i++) {
            ((RadioButton) (speedGroup.getChildAt(i))).setText((maxSpeed - 3 + i) + ":" + (maxSneak - i));
            ((RadioButton) (fightGroup.getChildAt(i))).setText((maxFight - 3 + i) + ":" + (maxWill - i));
            ((RadioButton) (loreGroup.getChildAt(i))).setText((maxLore - 3 + i) + ":" + (maxLuck - i));
        }

        Button money = (Button) rootView.findViewById(R.id.money);
        money.setText("$" + player.getInt(player.getColumnIndex(DBHelper.COL_MONEY)));
        /*
        NumberPicker money = (NumberPicker)rootView.findViewById(R.id.money);
        money.setMinValue(0);
        money.setMaxValue(100);
        money.setValue(player.getInt(player.getColumnIndex(DBHelper.COL_MONEY)));
        money.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ContentResolver resolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_MONEY, newVal);
                Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                        .appendPath(""+gameKey).appendPath("players").appendPath(""+playerKey)
                        .build();
                resolver.update(uri,values, null, null);
            }
        });
        */

        NumberPicker clues = (NumberPicker) rootView.findViewById(R.id.clues);
        clues.setMinValue(0);
        clues.setMaxValue(100);
        clues.setValue(player.getInt(player.getColumnIndex(DBHelper.COL_CLUES)));
        clues.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ContentResolver resolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_CLUES, newVal);
                Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                        .appendPath("" + gameKey).appendPath("players").appendPath("" + playerKey)
                        .build();
                resolver.update(uri, values, null, null);
            }
        });

        blessed = (CheckBox) rootView.findViewById(R.id.blessed);
        cursed = (CheckBox) rootView.findViewById(R.id.cursed);
        int blessStatus = player.getInt(player.getColumnIndex(DBHelper.COL_BLESSED));
        blessed.setChecked(blessStatus == 1);
        cursed.setChecked(blessStatus == 2);
        blessed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBlessStatus((((CheckBox) v).isChecked()) ? 1 : 0);
            }
        });
        cursed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBlessStatus((((CheckBox) v).isChecked()) ? 2 : 0);
            }
        });

        return rootView;
    }

    private void setBlessStatus(int value) {
        System.err.println("value = " + value);
        blessed.setChecked(value == 1);
        cursed.setChecked(value == 2);

        ContentResolver resolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_BLESSED, value);
        Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                .appendPath("" + gameKey).appendPath("players").appendPath("" + playerKey)
                .build();
        resolver.update(uri, values, null, null);
    }
}
