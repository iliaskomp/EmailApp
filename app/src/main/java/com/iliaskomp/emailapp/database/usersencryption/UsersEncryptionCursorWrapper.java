package com.iliaskomp.emailapp.database.usersencryption;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.iliaskomp.emailapp.models.UsersEncryptionEntry;

import java.util.UUID;

/**
 * Created by IliasKomp on 28/05/17.
 */

public class UsersEncryptionCursorWrapper extends CursorWrapper {

    public UsersEncryptionCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public UsersEncryptionEntry getUsersEncryptionEntry() {
        String uuidString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.UUID));
        String myEmailString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.MY_EMAIL));
        String theirEmailString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.THEIR_EMAIL));
        String myPublicKeyString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.MY_PUBLIC_KEY));
        String myPrivateKeyString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.MY_PRIVATE_KEY));
        String theirPublicKeyString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.THEIR_PUBLIC_KEY));
        String sharedSecretKeyString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.SHARED_SECRET_KEY));
        String stateString = getString(getColumnIndex(UsersEncryptionSchema.UsersTable.Cols.STATE));

        UsersEncryptionEntry entry = new UsersEncryptionEntry(UUID.fromString(uuidString), myEmailString, theirEmailString);
        entry.setMyPublicKey(myPublicKeyString);
        entry.setMyPrivateKey(myPrivateKeyString);
        entry.setTheirPublicKey(theirPublicKeyString);
        entry.setSharedSecretKey(sharedSecretKeyString);
        entry.setState(stateString);

        return entry;
    }
}
