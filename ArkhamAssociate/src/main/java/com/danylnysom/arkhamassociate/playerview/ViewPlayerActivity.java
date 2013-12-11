package com.danylnysom.arkhamassociate.playerview;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.danylnysom.arkhamassociate.R;
import com.danylnysom.arkhamassociate.db.ArkhamProvider;
import com.danylnysom.arkhamassociate.db.DBHelper;
import com.danylnysom.arkhamassociate.db.PlayerStats;

import java.util.Random;

public class ViewPlayerActivity extends FragmentActivity
        implements ActionBar.TabListener, PlayerStats, LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID_PLAYER = 1;
    public static final int LOADER_ID_INVESTIGATOR = 2;

    public Cursor player;
    public Cursor investigator;

    public static final String ARG_KEY = "key";
    public static final String ARG_URI = "uri";
    public static final String ARG_TAB = "tab";

    private ViewPlayerPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private int selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_player);

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(ARG_TAB, 0);
        }

        LoaderManager.LoaderCallbacks<Cursor> mCallbacks = this;
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_PLAYER, null, mCallbacks);
        lm.initLoader(LOADER_ID_INVESTIGATOR, null, mCallbacks);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public void onStop() {
        super.onStop();
        LoaderManager lm = getLoaderManager();
        lm.destroyLoader(LOADER_ID_INVESTIGATOR);
        lm.destroyLoader(LOADER_ID_PLAYER);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_TAB, mViewPager.getCurrentItem());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        selectedTab = savedInstanceState.getInt(ARG_TAB, 0);
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
//                int key = getIntent().getIntExtra(ARG_KEY, -1);
//                Uri uri = Uri.parse(getIntent().getStringExtra(ARG_URI) + "/" + key);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int selectedInvestigator;

    private void showInvestigatorPicker() {
        final Cursor cursor = getContentResolver().query(ArkhamProvider.INVESTIGATORS_URI, null,//PROJECTION,
                null, null, DBHelper.COL_KEY);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select an Investigator");
        selectedInvestigator = -1;
        dialog.setSingleChoiceItems(cursor, selectedInvestigator, DBHelper.COL_NAME, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedInvestigator = which;
            }
        });

        dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedInvestigator >= 0) {
                    cursor.moveToPosition(selectedInvestigator);
                    setInvestigator(cursor);
                } else {
                    Toast.makeText(
                            ((AlertDialog) (dialog)).getContext(),
                            "No investigator selected; player not modified",
                            Toast.LENGTH_SHORT
                    ).show();
                }
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

        // Restart the activity
//        Intent intent = new Intent(this, ViewPlayerActivity.class);
//        Bundle extras = getIntent().getExtras();
//        extras.putInt(ARG_TAB, mViewPager.getCurrentItem());
//        intent.putExtras(extras);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        finish();
//        startActivity(intent);

        //recreate();

        LoaderManager lm = getLoaderManager();
        lm.restartLoader(LOADER_ID_PLAYER, null, this);
        lm.restartLoader(LOADER_ID_INVESTIGATOR, null, this);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        mPagerAdapter.notifyDataSetChanged();
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
                args.putInt(ChangeValueFragment.MONEY_ARG,
                        player.getInt(player.getColumnIndex(DBHelper.COL_MONEY)));
                break;
            default:
                return;
        }
        fragment.setArguments(args);
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int playerKey = getIntent().getIntExtra(ARG_KEY, -1);
        Uri playerUri = Uri.parse(getIntent().getStringExtra(ARG_URI) + "/" + playerKey);
        CursorLoader loader = null;
        switch (id) {
            case LOADER_ID_PLAYER:
                player = null;
                loader = new CursorLoader(this, playerUri, null, null, null, null);
                break;
            case LOADER_ID_INVESTIGATOR:
                investigator = null;
                ContentResolver resolver = getContentResolver();
                Cursor tmp = resolver.query(playerUri, null, null, null, null);
                tmp.moveToFirst();
                String[] investigatorName = {tmp.getString(tmp.getColumnIndex(DBHelper.COL_INVESTIGATOR))};
                tmp.close();
                loader = new CursorLoader(this, ArkhamProvider.INVESTIGATORS_URI, null,
                        DBHelper.COL_NAME + " = ?", investigatorName, null);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        switch (loader.getId()) {
            case LOADER_ID_PLAYER:
                if (player != null && !player.isClosed()) {
                    player.close();
                }
                player = data;
                getActionBar().setTitle(player.getString(player.getColumnIndex(DBHelper.COL_NAME)));
                break;
            case LOADER_ID_INVESTIGATOR:
                if (investigator != null && !investigator.isClosed()) {
                    investigator.close();
                }
                investigator = data;
                break;
        }

        if (player != null && investigator != null) {
            final ActionBar actionBar = getActionBar();
            if (mPagerAdapter == null) {
                mPagerAdapter = new ViewPlayerPagerAdapter(getSupportFragmentManager());
            } else {
                mPagerAdapter.notifyDataSetChanged();
            }
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                    selectedTab = position;
                }
            });

            for (int i = 0; actionBar.getTabCount() < mPagerAdapter.getCount(); i++) {
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
            mViewPager.setAdapter(mPagerAdapter);

            mViewPager.setSaveEnabled(false);

//            ((ViewPlayerFragment)mPagerAdapter.getItem(selectedTab));

            actionBar.selectTab(actionBar.getTabAt(selectedTab));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ID_PLAYER:
                player.close();
                player = null;
                break;
            case LOADER_ID_INVESTIGATOR:
                investigator.close();
                investigator = null;
                break;
        }
    }

    public class ViewPlayerPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPlayerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
//            if(player != null && investigator != null) {
//                ((ViewPlayerFragment)object).update(player, investigator);
//                return super.getItemPosition(object);
//            } else {
            return FragmentStatePagerAdapter.POSITION_NONE;
//            }
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new ViewPlayerFragment1();
                    break;
                case 1:
                    frag = new ViewPlayerFragment2();
                    break;
                case 2:
                    frag = new ViewPlayerFragment3();
                    break;
            }
            if (player != null && investigator != null) {
                ((ViewPlayerFragment) (frag)).update(player, investigator);
            }
            frag.setRetainInstance(false);
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
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
