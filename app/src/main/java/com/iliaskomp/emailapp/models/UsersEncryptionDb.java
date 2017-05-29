package com.iliaskomp.emailapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iliaskomp.emailapp.database.usersencryption.UsersEncryptionBaseHelper;
import com.iliaskomp.emailapp.database.usersencryption.UsersEncryptionCursorWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.iliaskomp.emailapp.database.usersencryption.UsersEncryptionSchema.UsersTable;

/**
 * Created by IliasKomp on 28/05/17.
 */

public class UsersEncryptionDb {
    private static UsersEncryptionDb sUsersEncryptionDb;
    private static SQLiteDatabase sDatabase;

    public static UsersEncryptionDb get(Context context) {
        if (sUsersEncryptionDb == null) {
            sUsersEncryptionDb = new UsersEncryptionDb(context);
        }

        return sUsersEncryptionDb;
    }



    public String getEncryptionStateFromEmails (String myEmail, String theirEmail) {
        return getEntryFromEmails(myEmail, theirEmail).getState();
    }

    public String getSharedSeretFromEmails (String myEmail, String theirEmail) {
        return getEntryFromEmails(myEmail, theirEmail).getSharedSecretKey();
    }

    private UsersEncryptionDb(Context context) {
        sDatabase = new UsersEncryptionBaseHelper(context).getWritableDatabase();
    }

    public List<UsersEncryptionEntry> getUsersEncryptionEntries() {
        List<UsersEncryptionEntry> userEntries = new ArrayList<>();

        UsersEncryptionCursorWrapper cursor = queryUsersEncryptionEntries(null, null);

        try {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                userEntries.add(cursor.getUsersEncryptionEntry());
                cursor.moveToPrevious();
            }
        } finally {
            cursor.close();
        }

        return userEntries;
    }

    public void addEntry(UsersEncryptionEntry entry) {
        ContentValues values = getContentValues(entry);
        sDatabase.insert(UsersTable.NAME, null, values);
    }

    public int getUsersEncryptionEntriesCount() {
        return getUsersEncryptionEntries().size();
    }

    private UsersEncryptionEntry getEntryFromEmails (String myEmail, String theirEmail) {
        UsersEncryptionCursorWrapper cursor = queryUsersEncryptionEntries(
                UsersTable.Cols.MY_EMAIL + "= ? AND " + UsersTable.Cols.THEIR_EMAIL + "= ?",
                new String[] {myEmail, theirEmail});


        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getUsersEncryptionEntry();
        } finally {
            cursor.close();
        }
    }

    private UsersEncryptionCursorWrapper queryUsersEncryptionEntries(String whereClause, String[] whereArgs) {
        Cursor cursor = sDatabase.query(
                UsersTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new UsersEncryptionCursorWrapper(cursor);
    }

    private ContentValues getContentValues(UsersEncryptionEntry entry) {
        ContentValues values = new ContentValues();

        values.put(UsersTable.Cols.UUID, entry.getId().toString());
        values.put(UsersTable.Cols.MY_EMAIL, entry.getMyEmail());
        values.put(UsersTable.Cols.THEIR_EMAIL, entry.getTheirEmail());
        values.put(UsersTable.Cols.MY_PUBLIC_KEY, entry.getMyPublicKey());
        values.put(UsersTable.Cols.MY_PRIVATE_KEY, entry.getMyPrivateKey());
        values.put(UsersTable.Cols.THEIR_PUBLIC_KEY, entry.getTheirPublicKey());
        values.put(UsersTable.Cols.SHARED_SECRET_KEY, entry.getSharedSecretKey());
        values.put(UsersTable.Cols.STATE, entry.getState());

        return values;
    }
}
