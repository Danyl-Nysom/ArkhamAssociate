package com.danylnysom.arkhamassociate.playerview;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.danylnysom.arkhamassociate.R;
import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;
import com.danylnysom.arkhamassociate.db.PlayerStats;

public class ViewPlayerFragment2 extends Fragment implements PlayerStats, ViewPlayerFragment {
    private Cursor investigator;
    private Cursor player;

    private CheckBox blessed;
    private CheckBox cursed;
    private int gameKey;
    private int playerKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = getView();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_player_2, null);
        }

        Typeface face = Typeface.createFromAsset(rootView.getContext().getAssets(),
                "typeface.ttf");

        if (investigator != null && player != null) {
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

            final int curStamina = player.getInt(player.getColumnIndex(DBHelper.COL_STAMINA));
            final int curSanity = player.getInt(player.getColumnIndex(DBHelper.COL_SANITY));
            final int maxStamina = player.getInt(player.getColumnIndex(DBHelper.COL_STAMINA_MAX));
            final int maxSanity = player.getInt(player.getColumnIndex(DBHelper.COL_SANITY_MAX));

            playerKey = player.getInt(player.getColumnIndex(DBHelper.COL_KEY));
            gameKey = player.getInt(player.getColumnIndex(DBHelper.COL_GAME));

            TextView speedLabel = (TextView) rootView.findViewById(R.id.speedLabel);
            speedLabel.setText("Speed " + speed);
            speedLabel.setTypeface(face);
            TextView sneakLabel = (TextView) rootView.findViewById(R.id.sneakLabel);
            sneakLabel.setText("Sneak " + (maxSneak - 3 + maxSpeed - speed));
            sneakLabel.setTypeface(face);
            Button speedUp = (Button) rootView.findViewById(R.id.speedUp);
            Button sneakUp = (Button) rootView.findViewById(R.id.sneakUp);

            speedUp.setEnabled(speed < maxSpeed);
            sneakUp.setEnabled(speed > maxSpeed - 3);

            speedUp.setOnClickListener(new StatSliderListener
                    (speed, SPEED_SHIFT, stats, true, DBHelper.COL_STATS)
            );

            sneakUp.setOnClickListener(new StatSliderListener
                    (speed, SPEED_SHIFT, stats, false, DBHelper.COL_STATS)
            );

            TextView fightLabel = (TextView) rootView.findViewById(R.id.fightLabel);
            fightLabel.setText("Fight " + fight);
            fightLabel.setTypeface(face);
            TextView willLabel = (TextView) rootView.findViewById(R.id.willLabel);
            willLabel.setText("Will " + (maxWill - 3 + maxFight - fight));
            willLabel.setTypeface(face);
            Button fightUp = (Button) rootView.findViewById(R.id.fightUp);
            Button willUp = (Button) rootView.findViewById(R.id.willUp);

            fightUp.setEnabled(fight < maxFight);
            willUp.setEnabled(fight > maxFight - 3);

            fightUp.setOnClickListener(new StatSliderListener
                    (fight, FIGHT_SHIFT, stats, true, DBHelper.COL_STATS)
            );

            willUp.setOnClickListener(new StatSliderListener
                    (fight, FIGHT_SHIFT, stats, false, DBHelper.COL_STATS)
            );

            TextView loreLabel = (TextView) rootView.findViewById(R.id.loreLabel);
            loreLabel.setText("Lore " + lore);
            loreLabel.setTypeface(face);
            TextView luckLabel = (TextView) rootView.findViewById(R.id.luckLabel);
            luckLabel.setText("Luck " + (maxLuck - 3 + maxLore - lore));
            luckLabel.setTypeface(face);
            Button loreUp = (Button) rootView.findViewById(R.id.loreUp);
            Button luckUp = (Button) rootView.findViewById(R.id.luckUp);

            loreUp.setEnabled(lore < maxLore);
            luckUp.setEnabled(lore > maxLore - 3);

            loreUp.setOnClickListener(new StatSliderListener
                    (lore, LORE_SHIFT, stats, true, DBHelper.COL_STATS)
            );

            luckUp.setOnClickListener(new StatSliderListener
                    (lore, LORE_SHIFT, stats, false, DBHelper.COL_STATS)
            );

            blessed = (CheckBox) rootView.findViewById(R.id.blessed);
            cursed = (CheckBox) rootView.findViewById(R.id.cursed);
            int blessStatus = player.getInt(player.getColumnIndex(DBHelper.COL_BLESSED));
            blessed.setChecked(blessStatus == 1);
            cursed.setChecked(blessStatus == 2);
            blessed.setTypeface(face);
            cursed.setTypeface(face);
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

            TextView staminaLabel = (TextView) rootView.findViewById(R.id.staminaLabel);
            staminaLabel.setText(curStamina + "/" + maxStamina);
            staminaLabel.setTextAppearance(staminaLabel.getContext(), android.R.style.TextAppearance_Medium);
            staminaLabel.setTypeface(face);

            TextView sanityLabel = (TextView) rootView.findViewById(R.id.sanityLabel);
            sanityLabel.setText(curSanity + "/" + maxSanity);
            sanityLabel.setTextAppearance(sanityLabel.getContext(), android.R.style.TextAppearance_Medium);
            sanityLabel.setTypeface(face);

            TextView moneyLabel = (TextView) rootView.findViewById(R.id.moneyLabel);
            moneyLabel.setText("$" + player.getInt(player.getColumnIndex(DBHelper.COL_MONEY)));
            moneyLabel.setTextAppearance(moneyLabel.getContext(), android.R.style.TextAppearance_Medium);
            moneyLabel.setTypeface(face);

            TextView cluesLabel = (TextView) rootView.findViewById(R.id.cluesLabel);
            cluesLabel.setText("" + player.getInt(player.getColumnIndex(DBHelper.COL_CLUES)));
            cluesLabel.setTextAppearance(cluesLabel.getContext(), android.R.style.TextAppearance_Medium);
            cluesLabel.setTypeface(face);
        }
        return rootView;
    }

    private void setBlessStatus(int value) {
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

    @Override
    public void update(Cursor player, Cursor investigator) {
        this.investigator = investigator;
        this.player = player;
    }

    private class StatSliderListener implements Button.OnClickListener {

        private int oldValue;
        private int shift, stats;
        private boolean increment;
        private String column;

        public StatSliderListener(int oldValue, int shift, int stats, boolean increment, String column) {
            this.oldValue = oldValue;
            this.shift = shift;
            this.stats = stats;
            this.increment = increment;
            this.column = column;
        }

        @Override
        public void onClick(View v) {
            int newValue = (increment) ? oldValue +1 : oldValue -1;
            int newStats = (stats ^ (oldValue << shift)) | (newValue << shift);
            ContentResolver resolver = getActivity().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(column, newStats);
            Uri uri = ArkhamProvider.GAMES_URI.buildUpon()
                    .appendPath("" + gameKey).appendPath("players").appendPath("" + playerKey)
                    .build();
            resolver.update(uri, values, null, null);
        }
    }
}
