package com.iliaskomp.emailapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iliaskomp.emailapp.database.EmailDbSchema.SentTable;

/**
 * Created by IliasKomp on 15/02/17.
 */

public class SentBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "sentEmailBase.db";

    public SentBaseHelper(Context context) {
        super(context, DATABASE_NAME, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SentTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                SentTable.Cols.UUID + ", " +
                SentTable.Cols.SENDER + ", " +
                SentTable.Cols.RECIPIENT + ", " +
                SentTable.Cols.SUBJECT + ", " +
                SentTable.Cols.MESSAGE + ", " +
                SentTable.Cols.DATE + ", " +
                SentTable.Cols.HEADERS +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
