package com.iliaskomp.emailapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iliaskomp.emailapp.database.EmailDbSchema.InboxTable;

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
        db.execSQL("create table " + InboxTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                InboxTable.Cols.UUID + ", " +
                InboxTable.Cols.SENDER + ", " +
                InboxTable.Cols.RECIPIENT + ", " +
                InboxTable.Cols.SUBJECT + ", " +
                InboxTable.Cols.MESSAGE + ", " +
                InboxTable.Cols.DATE + ", " +
                InboxTable.Cols.HEADERS +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
