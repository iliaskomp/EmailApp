package com.iliaskomp.emailapp.database.email;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Sent base helper. Creates the database.
 */
public class SentBaseHelper extends SQLiteOpenHelper {
    /**
     * The constant VERSION.
     */
    public static final int VERSION = 1;
    /**
     * The constant DATABASE_NAME.
     */
    public static final String DATABASE_NAME = "sentEmailBase.db";

    /**
     * Instantiates a new Sent base helper.
     *
     * @param context the context
     */
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
