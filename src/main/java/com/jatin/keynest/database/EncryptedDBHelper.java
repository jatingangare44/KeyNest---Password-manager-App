package com.jatin.keynest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EncryptedDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "keynest.db";
    private static final int DB_VERSION = 1;

    public EncryptedDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Password table
        db.execSQL("CREATE TABLE passwords (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "username TEXT," +
                "encryptedPassword TEXT" +
                ");");

        // Master table with security question + answer
        db.execSQL("CREATE TABLE master (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "masterPassword TEXT," +
                "securityQuestion TEXT," +
                "securityAnswer TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle schema changes here if needed
        db.execSQL("DROP TABLE IF EXISTS passwords");
        db.execSQL("DROP TABLE IF EXISTS master");
        onCreate(db);
    }
}
