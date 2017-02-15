package com.iliaskomp.emailapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iliaskomp.emailapp.database.EmailDbSchema.EmailTable;

/**
 * Created by IliasKomp on 15/02/17.
 */

public class EmailBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "emailBase.db";

    public EmailBaseHelper(Context context) {
        super(context, DATABASE_NAME, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + EmailTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                EmailTable.Cols.UUID + ", " +
                EmailTable.Cols.SENDER + ", " +
                EmailTable.Cols.RECIPIENT + ", " +
                EmailTable.Cols.SUBJECT + ", " +
                EmailTable.Cols.MESSAGE + ", " +
                EmailTable.Cols.DATE + ", " +
                EmailTable.Cols.HEADERS +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
