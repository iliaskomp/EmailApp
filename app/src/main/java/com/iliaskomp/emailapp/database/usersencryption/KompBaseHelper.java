package com.iliaskomp.emailapp.database.usersencryption;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Komp base helper. Creates the database.
 */
public class KompBaseHelper extends SQLiteOpenHelper {
    /**
     * The constant VERSION.
     */
    public static final int VERSION = 1;
    /**
     * The constant DATABASE_NAME.
     */
    public static final String DATABASE_NAME = "kompEncryptionBase.db";

    /**
     * Instantiates a new Komp base helper.
     *
     * @param context the context
     */
    public KompBaseHelper(Context context) {
        super(context, DATABASE_NAME, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + KompSchema.UsersTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                KompSchema.UsersTable.Cols.UUID + ", " +
                KompSchema.UsersTable.Cols.MY_EMAIL + ", " +
                KompSchema.UsersTable.Cols.THEIR_EMAIL + ", " +
                KompSchema.UsersTable.Cols.MY_PUBLIC_KEY + ", " +
                KompSchema.UsersTable.Cols.MY_PRIVATE_KEY + ", " +
                KompSchema.UsersTable.Cols.THEIR_PUBLIC_KEY + ", " +
                KompSchema.UsersTable.Cols.SHARED_SECRET_KEY + ", " +
                KompSchema.UsersTable.Cols.STATE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
