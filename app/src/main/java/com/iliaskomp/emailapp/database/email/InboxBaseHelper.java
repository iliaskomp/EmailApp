package com.iliaskomp.emailapp.database.email;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IliasKomp on 15/02/17.
 */

public class InboxBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "inboxEmailBase.db";

    public InboxBaseHelper(Context context) {
        super(context, DATABASE_NAME, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + EmailDbSchema.InboxTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                EmailDbSchema.InboxTable.Cols.UUID + ", " +
                EmailDbSchema.InboxTable.Cols.SENDER + ", " +
                EmailDbSchema.InboxTable.Cols.RECIPIENT + ", " +
                EmailDbSchema.InboxTable.Cols.SUBJECT + ", " +
                EmailDbSchema.InboxTable.Cols.MESSAGE + ", " +
                EmailDbSchema.InboxTable.Cols.DATE + ", " +
                EmailDbSchema.InboxTable.Cols.HEADERS +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
