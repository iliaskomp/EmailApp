package com.iliaskomp.emailapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iliaskomp.emailapp.database.usersencryption.KompBaseHelper;
import com.iliaskomp.emailapp.database.usersencryption.KompCursorWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.iliaskomp.emailapp.database.usersencryption.KompSchema.UsersTable;

/**
 * Database for the komp encryption entries.
 * This class provides easy access for converted database elements to objects.
 */

public class KompDb {
    private static KompDb sKompDb;
    private static SQLiteDatabase sDatabase;

    public static KompDb get(Context context) {
        if (sKompDb == null) {
            sKompDb = new KompDb(context);
        }

        return sKompDb;
    }



    public int getEncryptionStateFromEmails (String myEmail, String theirEmail) {
        return getEntryFromEmails(myEmail, theirEmail).getState();
    }

    public String getSecretKeyForEmails(String myEmail, String theirEmail) {
        return getEntryFromEmails(myEmail, theirEmail).getSharedSecretKey();
    }

    private KompDb(Context context) {
        sDatabase = new KompBaseHelper(context).getWritableDatabase();
    }

    public List<KompEntry> getAllKompEntries() {
        List<KompEntry> userEntries = new ArrayList<>();
        KompCursorWrapper cursor = queryKompEntries(null, null);

        try {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                userEntries.add(cursor.getKompEntry());
                cursor.moveToPrevious();
            }
        } finally {
            cursor.close();
        }

        return userEntries;
    }

    public void addEntry(KompEntry entry) {
        ContentValues values = getContentValues(entry);
        sDatabase.insert(UsersTable.NAME, null, values);
    }

    public void updateEntry(String myEmail, String theirEmail, KompEntry newEntry) {
        ContentValues newValues = getContentValues(newEntry);

        sDatabase.update(UsersTable.NAME, newValues,
                UsersTable.Cols.MY_EMAIL + "= ? AND " + UsersTable.Cols.THEIR_EMAIL + "= ?",
                new String[] {myEmail, theirEmail});

    }

    public KompEntry getEntryFromEmails (String myEmail, String theirEmail) {
        KompCursorWrapper cursor = queryKompEntries(
                UsersTable.Cols.MY_EMAIL + "= ? AND " + UsersTable.Cols.THEIR_EMAIL + "= ?",
                new String[] {myEmail, theirEmail});


        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getKompEntry();
        } finally {
            cursor.close();
        }
    }

    private KompCursorWrapper queryKompEntries(String whereClause, String[] whereArgs) {
        Cursor cursor = sDatabase.query(
                UsersTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new KompCursorWrapper(cursor);
    }

    private ContentValues getContentValues(KompEntry entry) {
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
