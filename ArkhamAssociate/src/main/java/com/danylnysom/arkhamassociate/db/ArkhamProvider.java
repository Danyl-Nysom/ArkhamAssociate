package com.danylnysom.arkhamassociate.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.List;

/**
 * Created by Dylan on 17/11/13.
 */
public class ArkhamProvider extends ContentProvider {
    private DBHelper db;
    public static final String AUTHORITY = "com.danylnysom.arkhamassociate.db.ArkhamProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri GAMES_URI = Uri.parse("content://" + AUTHORITY + "/games");
    public static final Uri INVESTIGATORS_URI = Uri.parse("content://" + AUTHORITY + "/investigators");

    public static final String INVESTIGATOR_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "investigator";
    public static final String INVESTIGATORS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "investigator";
    public static final String GAME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "game";
    public static final String GAMES_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "game";
    public static final String PLAYER_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "player";
    public static final String PLAYERS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "player";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int INVESTIGATORS = 1;
    private static final int INVESTIGATORS_ID = 2;
    private static final int GAMES = 3;
    private static final int GAMES_ID = 4;
    private static final int PLAYERS = 5;
    private static final int PLAYERS_ID = 6;

    @Override
    public boolean onCreate() {
        db = new DBHelper(getContext());
        uriMatcher.addURI(AUTHORITY, "games", GAMES);
        uriMatcher.addURI(AUTHORITY, "games/#", GAMES_ID);
        uriMatcher.addURI(AUTHORITY, "games/#/players", PLAYERS);
        uriMatcher.addURI(AUTHORITY, "games/#/players/#", PLAYERS_ID);
        uriMatcher.addURI(AUTHORITY, "investigators", INVESTIGATORS);
        uriMatcher.addURI(AUTHORITY, "investigators/#", INVESTIGATORS_ID);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor data = null;
        List<String> segments;
        switch (uriMatcher.match(uri)) {
            case GAMES:
                data = db.getReadableDatabase().query(DBHelper.GAME_TABLE, null, selection, selectionArgs, null, null, DBHelper.COL_CREATION);
                break;
            case GAMES_ID:
                String[] gameId = {uri.getLastPathSegment()};
                data = db.getReadableDatabase().query(DBHelper.GAME_TABLE, null, DBHelper.COL_KEY + " = ?", gameId, null, null, null);
                break;
            case PLAYERS:
                segments = uri.getPathSegments();
                String[] playersGameId = {segments.get(1)};
                data = db.getReadableDatabase().query(DBHelper.PLAYER_TABLE, null, DBHelper.COL_GAME + " = ?", playersGameId, null, null, DBHelper.COL_NAME);
                break;
            case PLAYERS_ID:
                String[] playersIdParams = {uri.getLastPathSegment()};
                data = db.getReadableDatabase().query(DBHelper.PLAYER_TABLE, null, DBHelper.COL_KEY + " = ?", playersIdParams, null, null, null);
                break;
            case INVESTIGATORS:
                data = db.getReadableDatabase().query(DBHelper.INVESTIGATOR_TABLE, null, selection, selectionArgs, null, null, DBHelper.COL_NAME);
                break;
            case INVESTIGATORS_ID:
                String[] investigatorsIdParams = {uri.getLastPathSegment()};
                data = db.getReadableDatabase().query(DBHelper.INVESTIGATOR_TABLE, null, DBHelper.COL_KEY + " = ?", investigatorsIdParams, null, null, null);
                break;
        }
        data.setNotificationUri(getContext().getContentResolver(), uri);
        return data;
    }

    @Override
    public String getType(Uri uri) {
        String type = null;
        switch (uriMatcher.match(uri)) {
            case GAMES:
                type = GAMES_TYPE;
                break;
            case GAMES_ID:
                type = GAME_TYPE;
                break;
            case PLAYERS:
                type = PLAYERS_TYPE;
                break;
            case PLAYERS_ID:
                type = PLAYER_TYPE;
                break;
            case INVESTIGATORS:
                type = INVESTIGATORS_TYPE;
                break;
            case INVESTIGATORS_ID:
                type = INVESTIGATOR_TYPE;
                break;
        }
        return type;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = db.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case GAMES:
                database.insert(DBHelper.GAME_TABLE, null, values);
//                database.execSQL("INSERT INTO " + DBHelper.GAME_TABLE + " VALUES(NULL, NULL, '" +
                //                      values.getAsString(DBHelper.COL_NAME) + "', " + values.getAsLong(DBHelper.COL_CREATION) + ")");
                break;
            case PLAYERS:
                if (values.containsKey(DBHelper.COL_INVESTIGATOR)) {
                    String[] columns = {DBHelper.COL_STATS, DBHelper.COL_NAME};
                    String[] playersIdParams = {values.getAsString(DBHelper.COL_INVESTIGATOR)};
                    Cursor investigator = database.query(
                            DBHelper.INVESTIGATOR_TABLE, columns, DBHelper.COL_KEY + " = ?", playersIdParams, null, null, null, null);
                    investigator.moveToFirst();
                    values.put(DBHelper.COL_STATS, investigator.getInt(investigator.getColumnIndex(DBHelper.COL_STATS)));
                    values.put(DBHelper.COL_CLUES, investigator.getInt(investigator.getColumnIndex(DBHelper.COL_CLUES)));

                    database.execSQL("INSERT INTO " + DBHelper.PLAYER_TABLE + " VALUES(NULL, NULL, '" +
                            values.getAsString(DBHelper.COL_NAME) + "', " + investigator.getInt(investigator.getColumnIndex(DBHelper.COL_STATS)) + ", " +
                            values.getAsInteger(DBHelper.COL_GAME) + ", '" + values.getAsString(DBHelper.COL_INVESTIGATOR) + "');");
                    investigator.close();
                }
                //   database.execSQL("INSERT INTO " + DBHelper.PLAYER_TABLE + " VALUES(NULL, NULL, '" +
                //         values.getAsString(DBHelper.COL_NAME) + "', 0, " + values.getAsInteger(DBHelper.COL_GAME) + ", NULL);");
                //}
                database.insert(DBHelper.PLAYER_TABLE, null, values);
                break;
            case INVESTIGATORS:
                break;
        }
        database.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        String table = null;
        String whereClause = null;
        String[] whereArgs = {""};
        switch (uriMatcher.match(uri)) {
            case GAMES:
                table = DBHelper.GAME_TABLE;
                whereClause = selection;
                whereArgs = selectionArgs;
                break;
            case GAMES_ID:
                table = DBHelper.GAME_TABLE;
                whereClause = DBHelper.COL_KEY + " = ?";
                whereArgs[0] = uri.getLastPathSegment();
                break;
            case PLAYERS:
                table = DBHelper.PLAYER_TABLE;
                whereClause = DBHelper.COL_GAME + " = ?";
                whereArgs[0] = uri.getPathSegments().get(1);
                break;
            case PLAYERS_ID:
                table = DBHelper.PLAYER_TABLE;
                whereClause = DBHelper.COL_KEY + " = ?";
                whereArgs[0] = uri.getLastPathSegment();
                break;
            case INVESTIGATORS:
                table = DBHelper.INVESTIGATOR_TABLE;
                whereClause = selection;
                whereArgs = selectionArgs;
                break;
            case INVESTIGATORS_ID:
                table = DBHelper.INVESTIGATOR_TABLE;
                whereClause = DBHelper.COL_KEY + " = ?";
                whereArgs[0] = uri.getLastPathSegment();
                break;
        }
        int count = database.delete(table, whereClause, whereArgs);
        database.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        String table = null;
        String whereClause = null;
        String[] whereArgs = {""};
        switch (uriMatcher.match(uri)) {
            case GAMES:
                table = DBHelper.GAME_TABLE;
                whereClause = selection;
                whereArgs = selectionArgs;
                break;
            case GAMES_ID:
                table = DBHelper.GAME_TABLE;
                whereClause = DBHelper.COL_KEY + " = ?";
                whereArgs[0] = uri.getLastPathSegment();
                break;
            case PLAYERS:
                table = DBHelper.PLAYER_TABLE;
                whereClause = DBHelper.COL_GAME + " = ?";
                whereArgs[0] = uri.getPathSegments().get(1);
                break;
            case PLAYERS_ID:
                table = DBHelper.PLAYER_TABLE;
                whereClause = DBHelper.COL_KEY + " = ?";
                whereArgs[0] = uri.getLastPathSegment();
                break;
            case INVESTIGATORS:
                table = DBHelper.INVESTIGATOR_TABLE;
                whereClause = selection;
                whereArgs = selectionArgs;
                break;
            case INVESTIGATORS_ID:
                table = DBHelper.INVESTIGATOR_TABLE;
                whereClause = DBHelper.COL_KEY + " = ?";
                whereArgs[0] = uri.getLastPathSegment();
                break;
        }
        int count = database.update(table, values, whereClause, whereArgs);
        database.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
