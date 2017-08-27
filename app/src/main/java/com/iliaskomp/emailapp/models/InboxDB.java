package com.iliaskomp.emailapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iliaskomp.emailapp.database.email.InboxBaseHelper;
import com.iliaskomp.emailapp.database.email.EmailCursorWrapper;
import com.iliaskomp.emailapp.database.email.EmailDbSchema.InboxTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Inbox Database that implements the Email Database.
 * This class provides easy access for converted database elements to objects.
 */
public class InboxDB implements EmailDB {
    private static InboxDB sInboxDB;
    private Context mContext;
    private static SQLiteDatabase sDatabase;

    /**
     * Get inbox database.
     *
     * @param context the context
     * @return the inbox db
     */
    public static InboxDB get(Context context) {
        if (sInboxDB == null) {
            sInboxDB = new InboxDB(context);
        }

        return sInboxDB;
    }

    private InboxDB(Context context) {
        mContext = context.getApplicationContext();
        sDatabase = new InboxBaseHelper(mContext).getWritableDatabase();
    }

    @Override
    public EmailModel getEmailFromId (UUID id) {
        EmailCursorWrapper cursor = queryEmails(
                InboxTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getInboxEmail();
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<EmailModel> getEmails() {
        List<EmailModel> emails = new ArrayList<>();

        EmailCursorWrapper cursor = queryEmails(null, null);

        try {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                emails.add(cursor.getInboxEmail());
                cursor.moveToPrevious();
            }
        } finally {
            cursor.close();
        }

        return emails;
    }

    @Override
    public void addEmail(EmailModel email) {
        ContentValues values = getContentValues(email);
        sDatabase.insert(InboxTable.NAME, null, values);
    }

    @Override
    public int getEmailCount() {
        return getEmails().size();
    }

    private EmailCursorWrapper queryEmails(String whereClause, String[] whereArgs) {
        Cursor cursor = sDatabase.query(
                InboxTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new EmailCursorWrapper(cursor);
    }

    private ContentValues getContentValues(EmailModel email) {
        ContentValues values = new ContentValues();
        values.put(InboxTable.Cols.UUID, email.getId().toString());
        values.put(InboxTable.Cols.SENDER, email.getSender());
        values.put(InboxTable.Cols.RECIPIENT, email.getRecipient());
        values.put(InboxTable.Cols.SUBJECT, email.getSubject());
        values.put(InboxTable.Cols.MESSAGE, email.getMessage());
        values.put(InboxTable.Cols.DATE, email.getFullDate().toString());
        values.put(InboxTable.Cols.HEADERS, email.getHeaders());

        return values;
    }
}
