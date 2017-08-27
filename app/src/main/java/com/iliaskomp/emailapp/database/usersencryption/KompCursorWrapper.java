package com.iliaskomp.emailapp.database.usersencryption;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.iliaskomp.emailapp.models.KompEntry;

import java.util.UUID;

/**
 * Converts database objects to java objects for easier access.
 */
public class KompCursorWrapper extends CursorWrapper {

    /**
     * Instantiates a new Komp cursor wrapper.
     *
     * @param cursor the cursor
     */
    public KompCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Gets komp entry.
     *
     * @return the komp entry
     */
    public KompEntry getKompEntry() {
        String uuidString = getString(getColumnIndex(KompSchema.UsersTable.Cols.UUID));
        String myEmailString = getString(getColumnIndex(KompSchema.UsersTable.Cols.MY_EMAIL));
        String theirEmailString = getString(getColumnIndex(KompSchema.UsersTable.Cols.THEIR_EMAIL));
        String myPublicKeyString = getString(getColumnIndex(KompSchema.UsersTable.Cols.MY_PUBLIC_KEY));
        String myPrivateKeyString = getString(getColumnIndex(KompSchema.UsersTable.Cols.MY_PRIVATE_KEY));
        String theirPublicKeyString = getString(getColumnIndex(KompSchema.UsersTable.Cols.THEIR_PUBLIC_KEY));
        String sharedSecretKeyString = getString(getColumnIndex(KompSchema.UsersTable.Cols.SHARED_SECRET_KEY));
        int stateString = getInt(getColumnIndex(KompSchema.UsersTable.Cols.STATE));

        KompEntry entry = new KompEntry(UUID.fromString(uuidString), myEmailString, theirEmailString);
        entry.setMyPublicKey(myPublicKeyString);
        entry.setMyPrivateKey(myPrivateKeyString);
        entry.setTheirPublicKey(theirPublicKeyString);
        entry.setSharedSecretKey(sharedSecretKeyString);
        entry.setState(stateString);

        return entry;
    }
}
