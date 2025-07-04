package com.example.sehs4542group3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Existing user table constants
    private static final String TABLE_USERS = "users";
    static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // New score table constants
    private static final String TABLE_SCORES = "scores";
    private static final String COL_GAME_ID = "game_id";
    private static final String COL_SCORE = "score";
    private static final String COL_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, "GameDB", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USERNAME + " TEXT PRIMARY KEY, " +
                COL_PASSWORD + " TEXT)");

        // Create scores table
        db.execSQL("CREATE TABLE " + TABLE_SCORES + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_GAME_ID + " INTEGER, " +
                COL_SCORE + " INTEGER, " +
                COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COL_USERNAME + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USERNAME + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    // User authentication methods
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Score management methods
    public void saveScore(String username, int gameId, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_GAME_ID, gameId);
        values.put(COL_SCORE, score);
        db.insert(TABLE_SCORES, null, values);
    }

    public Cursor getHighScores(String username, int gameId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT _id, " + COL_USERNAME + ", " + COL_SCORE + ", " +
                        "strftime('%Y-%m-%d %H:%M', " + COL_TIMESTAMP + ") AS timestamp " +  // Explicit alias
                        "FROM " + TABLE_SCORES +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_GAME_ID + "=? " +
                        "ORDER BY " + COL_SCORE + " DESC LIMIT 10",
                new String[]{username, String.valueOf(gameId)}
        );
    }

    public int getHighScore(String username, int gameId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COL_SCORE + ") FROM " + TABLE_SCORES +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_GAME_ID + "=?",
                new String[]{username, String.valueOf(gameId)} // Fix: Ensure correct parameter order
        );
        int highScore = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            highScore = cursor.getInt(0);
        }
        cursor.close();
        return highScore;
    }

    public Cursor getGlobalHighScores(int gameId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT _id, " + COL_USERNAME + ", MAX(" + COL_SCORE + ") AS score, " +
                        "MAX(" + COL_TIMESTAMP + ") AS timestamp " +  // Explicit alias
                        "FROM " + TABLE_SCORES +
                        " WHERE " + COL_GAME_ID + "=? " +
                        "GROUP BY " + COL_USERNAME + " " +
                        "ORDER BY score DESC LIMIT 5",
                new String[]{String.valueOf(gameId)}
        );
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);
        int rowsAffected = db.update(TABLE_USERS, values, COL_USERNAME + " = ?", new String[]{username});
        return rowsAffected > 0;
    }

    public boolean updateUser(String oldUsername, String newUsername, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            if (!oldUsername.equals(newUsername)){
                if (checkUserExists(newUsername)) {
                    return false;
                }
            }

            ContentValues userValues = new ContentValues();
            userValues.put(COL_USERNAME, newUsername);
            userValues.put(COL_PASSWORD, newPassword);
            int userRows = db.update(TABLE_USERS, userValues, COL_USERNAME + " = ?", new String[]{oldUsername});
            if (userRows == 0) return false;

            if (!oldUsername.equals(newUsername)) {
                ContentValues scoreValues = new ContentValues();
                scoreValues.put(COL_USERNAME, newUsername);
                db.update(TABLE_SCORES, scoreValues, COL_USERNAME + " = ?", new String[]{oldUsername});
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public String getUserPassword(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_PASSWORD + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + " = ?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            String password = cursor.getString(0);
            cursor.close();
            return password;
        }
        cursor.close();
        return null;
    }
}