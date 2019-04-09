package com.trackersurvey.litepal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper中有两个抽象方法，分别是onCreate()和onUpgrade()
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String CREATE_NEWS = "create table news ("
            + "id integer primary key autoincrement, "
            + "title text, "
            + "content text, "
            + "publishdate integer,"
            + "commentcount integer)";

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}