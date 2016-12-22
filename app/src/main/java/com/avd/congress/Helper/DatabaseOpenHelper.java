package com.avd.congress.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by avdmy on 11/21/2016.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static DatabaseOpenHelper sInstance;
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_LEGISLATOR_TABLE = "create table legislator( bioguide_id text primary key, legislatorJSON text not null);";
    private static final String CREATE_BILL_TABLE = "create table bill (bill_id text primary key, billJSON text not null);";
    private static final String CREATE_COMMITTEE_TABLE = "CREATE TABLE COMMITTEE (committee_id TEXT PRIMARY KEY, committeeJSON TEXT NOT NULL);";

    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS legislator");
        db.execSQL("DROP TABLE IF EXISTS bill");
        db.execSQL("DROP TABLE IF EXISTS COMMITTEE");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LEGISLATOR_TABLE);
        db.execSQL(CREATE_BILL_TABLE);
        db.execSQL(CREATE_COMMITTEE_TABLE);
    }

    public static synchronized DatabaseOpenHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }
}
