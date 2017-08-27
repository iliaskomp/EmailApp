package com.iliaskomp.emailapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iliaskomp.emailapp.database.email.EmailCursorWrapper;
import com.iliaskomp.emailapp.database.email.EmailDbSchema.SentTable;
import com.iliaskomp.emailapp.database.email.SentBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sent folder database that implements the Email Database.
 * This class provides easy access for converted database elements to objects.
 */
public class SentDB implements EmailDB {
    private static SentDB sSentDB;
    private Context mContext;
    private static SQLiteDatabase sDatabase;

    /**
     * Get sent database.
     *
     * @param context the context
     * @return the sent db
     */
    public static SentDB get(Context context) {
        if (sSentDB == null) {
            sSentDB = new SentDB(context);
        }

        return sSentDB;
    }

    private SentDB(Context context) {
        mContext = context.getApplicationContext();
        sDatabase = new SentBaseHelper(mContext).getWritableDatabase();
    }

    @Override
    public EmailModel getEmailFromId (UUID id) {
        EmailCursorWrapper cursor = queryEmails(
                SentTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getSentEmail();
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
                emails.add(cursor.getSentEmail());
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
        sDatabase.insert(SentTable.NAME, null, values);
    }

    @Override
    public int getEmailCount() {
        return getEmails().size();
    }

    private EmailCursorWrapper queryEmails(String whereClause, String[] whereArgs) {
        Cursor cursor = sDatabase.query(
                SentTable.NAME,
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
        values.put(SentTable.Cols.UUID, email.getId().toString());
        values.put(SentTable.Cols.SENDER, email.getSender());
        values.put(SentTable.Cols.RECIPIENT, email.getRecipient());
        values.put(SentTable.Cols.SUBJECT, email.getSubject());
        values.put(SentTable.Cols.MESSAGE, email.getMessage());
        values.put(SentTable.Cols.DATE, email.getFullDate().toString());
        values.put(SentTable.Cols.HEADERS, email.getHeaders());

        return values;
    }
}
