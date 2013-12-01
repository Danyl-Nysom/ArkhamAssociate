package com.danylnysom.arkhamassociate;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;

import java.util.Locale;
import java.util.Random;

public class ViewPlayerActivity extends FragmentActivity implements ActionBar.TabListener, PlayerStats {

    private Cursor player;
    private Cursor investigator;

    public static final String ARG_KEY = "key";
    public static final String ARG_URI = "uri";

    ViewPlayerPagerAdapter mPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_player);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mPagerAdapter = new ViewPlayerPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        refreshCursors();
        getActionBar().setTitle(player.getString(player.getColumnIndex(DBHelper.COL_NAME)));
    }

    private void refreshCursors() {
        if (player != null && !player.isClosed()) {
            player.close();
        }
        if (investigator != null && !investigator.isClosed()) {
            investigator.close();
        }

        int playerKey = getIntent().getIntExtra(ARG_KEY, -1);
        Uri playerUri = Uri.parse(getIntent().getStringExtra(ARG_URI) + "/" + playerKey);
        ContentResolver resolver = this.getContentResolver();
        player = resolver.query(playerUri, null, null, null, null);
        player.moveToFirst();

        String[] investigatorName = {player.getString(player.getColumnIndex(DBHelper.COL_INVESTIGATOR))};
        investigator = resolver.query(ArkhamProvider.INVESTIGATORS_URI, null,
                DBHelper.COL_NAME + " = ?", investigatorName, null);
        investigator.moveToFirst();
    }

    @Override
    public void onStop() {
        super.onStop();
        player.close();
        investigator.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_change_investigator:
                showInvestigatorPicker();
                int key = getIntent().getIntExtra(ARG_KEY, -1);
                Uri uri = Uri.parse(getIntent().getStringExtra(ARG_URI) + "/" + key);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInvestigatorPicker() {
        final Cursor cursor = getContentResolver().query(ArkhamProvider.INVESTIGATORS_URI, null,//PROJECTION,
                null, null, DBHelper.COL_KEY);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select an Investigator");

        dialog.setSingleChoiceItems(cursor, 0, DBHelper.COL_NAME, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cursor.moveToPosition(which);
            }
        });

        dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setInvestigator(cursor);
            }
        });

        dialog.setNeutralButton("Randomize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = new Random().nextInt(new DBHelper(getApplicationContext()).getInvestigatorCount());
                cursor.moveToPosition(index);
                setInvestigator(cursor);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialog.show();
    }

    private void setInvestigator(Cursor cursor) {
        ContentResolver resolver = getContentResolver();
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

        int key = getIntent().getIntExtra(ARG_KEY, -1);
        Uri playerUri = Uri.parse(getIntent().getStringExtra(ARG_URI) + "/" + key);

        resolver.update(playerUri, values, null, null);
        cursor.close();
//        refreshCursors();
        finish();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void showChangeValuePopup(View v) {
        ChangeValueFragment fragment = new ChangeValueFragment();
        Bundle args = new Bundle();
        args.putInt(ChangeValueFragment.VIEW_ID_ARG, v.getId());
        args.putInt(ChangeValueFragment.PLAYER_KEY_ARG,
                player.getInt(player.getColumnIndex(DBHelper.COL_KEY)));
        args.putInt(ChangeValueFragment.GAME_KEY_ARG,
                player.getInt(player.getColumnIndex(DBHelper.COL_GAME)));

        switch (v.getId()) {
            case R.id.money:
                System.err.println("COOL");
                args.putInt(ChangeValueFragment.MONEY_ARG,
                        player.getInt(player.getColumnIndex(DBHelper.COL_MONEY)));
                break;
            default:
                System.err.println("UNCOOL");
                return;
        }
        fragment.setArguments(args);

//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        android.app.Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);

        // Create and show the dialog.
        fragment.show(getFragmentManager(), "dialog");
    }

    public class ViewPlayerPagerAdapter extends FragmentPagerAdapter {

        public ViewPlayerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new ViewPlayerFragment1(investigator);
                    break;
                case 1:
                    frag = new ViewPlayerFragment2(investigator, player);
                    break;
                case 2:
                    frag = new ViewPlayerFragment3(investigator);
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            String title = null;
            switch (position) {
                case 0:
                    title = "Overview";
                    break;
                case 1:
                    title = "Current Stats";
                    break;
                case 2:
                    title = "Story";
                    break;
            }
            return title;
        }
    }
}
