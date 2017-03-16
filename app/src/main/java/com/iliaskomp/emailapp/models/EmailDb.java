package com.iliaskomp.emailapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iliaskomp.emailapp.database.EmailBaseHelper;
import com.iliaskomp.emailapp.database.EmailCursorWrapper;
import com.iliaskomp.emailapp.database.EmailDbSchema.EmailTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class EmailDb {
    private static EmailDb sEmailDb;
    private Context mContext;
    private static SQLiteDatabase mDatabase;


//    public static SQLiteDatabase getEmailDB(Context context) {
//        if (mDatabase == null) {
//            EmailDb db = new EmailDb(context);
//        }
//        return mDatabase;
//    }

    public static EmailDb get(Context context) {
        if (sEmailDb == null) {
            sEmailDb = new EmailDb(context);
        }

        return sEmailDb;
    }

    private EmailDb(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new EmailBaseHelper(mContext).getWritableDatabase();
    }

    public static EmailModel getEmailFromId (UUID id) {
        EmailCursorWrapper cursor = queryEmails(
                EmailTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getEmail();
        } finally {
            cursor.close();
        }
    }

    public static List<EmailModel> getEmails() {
        List<EmailModel> emails = new ArrayList<>();

        EmailCursorWrapper cursor = queryEmails(null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            // get stuff from the cursor
        }

        try {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                emails.add(cursor.getEmail());
                cursor.moveToPrevious();
            }
        } finally {
            cursor.close();
        }


//        try {
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                emails.add(cursor.getEmail());
//                cursor.moveToNext();
//            }
//        } finally {
//            cursor.close();
//        }

        return emails;
    }

    public int getEmailCount() {
        return getEmails().size();
    }


    public void addEmail(EmailModel email) {
        ContentValues values = getContentValues(email);
        mDatabase.insert(EmailTable.NAME, null, values);
    }

    private static EmailCursorWrapper queryEmails(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                EmailTable.NAME,
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
        values.put(EmailTable.Cols.UUID, email.getId().toString());
        values.put(EmailTable.Cols.SENDER, email.getSender());
        values.put(EmailTable.Cols.RECIPIENT, email.getRecipient());
        values.put(EmailTable.Cols.SUBJECT, email.getSubject());
        values.put(EmailTable.Cols.MESSAGE, email.getMessage());
        values.put(EmailTable.Cols.DATE, email.getFullDate().toString());
        values.put(EmailTable.Cols.HEADERS, email.getHeaders());

        return values;
    }


}
