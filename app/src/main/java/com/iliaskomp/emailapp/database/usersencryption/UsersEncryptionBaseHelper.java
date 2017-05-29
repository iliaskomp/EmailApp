package com.iliaskomp.emailapp.database.usersencryption;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by IliasKomp on 28/05/17.
 */

public class UsersEncryptionBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "usersEncryptionBase.db";

    public UsersEncryptionBaseHelper(Context context) {
        super(context, DATABASE_NAME, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UsersEncryptionSchema.UsersTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                UsersEncryptionSchema.UsersTable.Cols.UUID + ", " +
                UsersEncryptionSchema.UsersTable.Cols.MY_EMAIL + ", " +
                UsersEncryptionSchema.UsersTable.Cols.THEIR_EMAIL + ", " +
                UsersEncryptionSchema.UsersTable.Cols.MY_PUBLIC_KEY + ", " +
                UsersEncryptionSchema.UsersTable.Cols.MY_PRIVATE_KEY + ", " +
                UsersEncryptionSchema.UsersTable.Cols.THEIR_PUBLIC_KEY + ", " +
                UsersEncryptionSchema.UsersTable.Cols.SHARED_SECRET_KEY + ", " +
                UsersEncryptionSchema.UsersTable.Cols.STATE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
