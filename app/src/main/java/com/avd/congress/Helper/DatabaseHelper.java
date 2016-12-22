package com.avd.congress.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by avdmy on 11/21/2016.
 */

public class DatabaseHelper {
    static SQLiteDatabase db;
    public static void init(Context context){
        if(db==null)
            db =  DatabaseOpenHelper.getInstance(context).getWritableDatabase();
    }

    public static Cursor select(String query){
        return db.rawQuery(query, null);
    }

    public static void execSQL(String query){
        db.execSQL(query);
    }
}
