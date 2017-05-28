package com.iliaskomp.emailapp.database.email;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL("create table " + EmailDbSchema.SentTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                EmailDbSchema.SentTable.Cols.UUID + ", " +
                EmailDbSchema.SentTable.Cols.SENDER + ", " +
                EmailDbSchema.SentTable.Cols.RECIPIENT + ", " +
                EmailDbSchema.SentTable.Cols.SUBJECT + ", " +
                EmailDbSchema.SentTable.Cols.MESSAGE + ", " +
                EmailDbSchema.SentTable.Cols.DATE + ", " +
                EmailDbSchema.SentTable.Cols.HEADERS +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
