package com.danylnysom.arkhamassociate.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.danylnysom.arkhamassociate.PlayerStats;

/**
 * Created by Dylan on 16/11/13.
 */
public class DBHelper extends SQLiteOpenHelper implements PlayerStats {
    private Context context;

    private static final String[] INVESTIGATOR_FILES = {
            "base.investigators"
    };

    private static final String DB_NAME = "arkhamdb";

    public static final String PLAYER_TABLE = "players";
    public static final String GAME_TABLE = "games";
    public static final String INVESTIGATOR_TABLE = "investigators";

    public static final String COL_ID = "_id";
    public static final String COL_KEY = "key";
    public static final String COL_NAME = "name";
    public static final String COL_CREATION = "creation";
    public static final String COL_GAME = "game";
    public static final String COL_INVESTIGATOR = "investigator";
    public static final String COL_HOME = "home";
    public static final String COL_POSSESSIONS_FIXED = "pos_fixed";
    public static final String COL_POSSESSIONS_RANDOM = "pos_random";
    public static final String COL_ABILITY = "ability";
    public static final String COL_STATS = "stats";

    private static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.err.println("----------create db----------");
        db.execSQL("CREATE TABLE " + INVESTIGATOR_TABLE + " (" +
                COL_KEY + " INTEGER PRIMARY KEY, " +
                COL_ID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_HOME + " TEXT, " +
                COL_STATS + " INTEGER, " +
                COL_POSSESSIONS_FIXED + " TEXT, " +
                COL_POSSESSIONS_RANDOM + " TEXT, " +
                COL_ABILITY + " TEXT);");

        db.execSQL("CREATE TABLE " + GAME_TABLE + " (" +
                COL_KEY + " INTEGER PRIMARY KEY, " +
                COL_ID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_CREATION + " INTEGER);");

        db.execSQL("CREATE TABLE " + PLAYER_TABLE + " (" +
                COL_KEY + " INTEGER PRIMARY KEY, " +
                COL_ID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_STATS + " INTEGER, " +
                COL_GAME + " INTEGER, " +
                COL_INVESTIGATOR + " STRING, " +
                "FOREIGN KEY(" + COL_GAME + ") REFERENCES " + GAME_TABLE + "(" + COL_KEY + ")," +
                "FOREIGN KEY(" + COL_INVESTIGATOR + ") REFERENCES " + INVESTIGATOR_TABLE + "(" + COL_NAME + "));");

        addInvestigators(db);
    }

    private void addInvestigators(SQLiteDatabase db) {
        switch (VERSION) {
            case 1:
                addInvestigator("Amanda Sharpe", "Bank of Arkham", 5, 5, 4, 4, 4, 4, 4, 4, 3,
                        "$1|1 Clue token", "1 Common item|1 Unique item|2 Skills|1 Spell",
                        "Studious - Whenever Amanda draws one or more cards from the Skill deck, she draws one extra card and then discards one of the cards.",
                        db);
                addInvestigator("\"Ashcan\" Pete", "River Docks", 4, 6, 3, 6, 5, 5, 3, 3, 1,
                        "$1|3 Clue tokens|1 Ally (Duke)", "1 Common item|1 Unique item|1 Skill",
                        "Scrounge - When Pete draws from the Common item, Unique item, or Spell deck, he may draw from either the top or the bottom of that deck, his choice. Pete may look at the bottom card of those decks at any time.",
                        db);
                /*
                db.execSQL("INSERT INTO " + INVESTIGATOR_TABLE +
                        " VALUES(NULL, NULL, 'Bob Jenkins', 'General Store', 4, 6, 1, '$9', '2 Common items|2 Unique items|1 Skill', 'Shrewd Dealer - Any Phase: Whenever Bob draws one or more cards from the Common item deck, he draws one extra card and then discards one of the cards.');");
                db.execSQL("INSERT INTO " + INVESTIGATOR_TABLE +
                        " VALUES(NULL, NULL, 'Carolyn Fern', 'Arkham Asylum', 6, 4, 2, '$7|1 Clue token', '2 Unique items|2 Common items|1 Skill', 'Psychology - Upkeep: Dr. Fern may restore 1 Sanity to herself or another character in her location. She cannot raise a character''s Sanity higher than that character''s maximum Sanity.');");
                db.execSQL("INSERT INTO " + INVESTIGATOR_TABLE +
                        " VALUES(NULL, NULL, 'Darrell Simmons', 'Newspaper', 4, 6, 2, '$4|1 Clue token|1 Special (Retainer)', '1 Common items|2 Unique items|1 Skill', 'Hometown Advantage - Town Encounter: When drawing location encounters in Arkham, Darrell draws two cards and may choose whichever one of the two he wants. This ability does not work when drawing gate encounters in Other Worlds.');");
                db.execSQL("INSERT INTO " + INVESTIGATOR_TABLE +
                        " VALUES(NULL, NULL, 'Dexter Drake', 'Ye Olde Magick Shoppe', 5, 5, 2, '$5|1 Spell (Shrivelling)', '1 Common items|1 Unique item|2 Spells|1 Skill', 'Magical Gift - Any Phase: Whenever \"The Great\" Drake draws one or more cards from the Spell deck, he draws one extra card and then discards one of the cards.');");
                */
        }
    }

    private void addInvestigator(String name, String home, int sanity, int stamina,
                                 int speed, int sneak, int fight, int will, int lore, int luck,
                                 int focus, String fixed, String random, String ability,
                                 SQLiteDatabase db) {
        int stats = (sanity << SANITY_SHIFT) + (stamina << STAMINA_SHIFT) +
                (speed << SPEED_SHIFT) + (sneak << SNEAK_SHIFT) +
                (fight << FIGHT_SHIFT) + (will << WILL_SHIFT) +
                (lore << LORE_SHIFT) + (luck << LUCK_SHIFT) +
                (focus << FOCUS_SHIFT);

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_HOME, home);
        values.put(COL_STATS, stats);
        values.put(COL_POSSESSIONS_FIXED, fixed);
        values.put(COL_POSSESSIONS_RANDOM, random);
        values.put(COL_ABILITY, ability);

        db.insert(INVESTIGATOR_TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int getInvestigatorCount() {
        SQLiteDatabase db = getReadableDatabase();
        int count = db.query(INVESTIGATOR_TABLE, null, null, null, null, null, null).getCount();
        db.close();
        return count;
    }
}
