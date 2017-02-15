package com.iliaskomp.emailapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.utils.DateFormatHelper;

import java.util.UUID;

/**
 * Created by IliasKomp on 15/02/17.
 */

public class EmailCursorWrapper extends CursorWrapper {

    public EmailCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public EmailModel getEmail() {
        String uuidString = getString(getColumnIndex(EmailDbSchema.EmailTable.Cols.UUID));
        String senderString = getString(getColumnIndex(EmailDbSchema.EmailTable.Cols.SENDER));
        String recipientString = getString(getColumnIndex(EmailDbSchema.EmailTable.Cols.RECIPIENT));
        String subjectString = getString(getColumnIndex(EmailDbSchema.EmailTable.Cols.SUBJECT));
        String messageString = getString(getColumnIndex(EmailDbSchema.EmailTable.Cols.MESSAGE));
        String dateString = getString(getColumnIndex(EmailDbSchema.EmailTable.Cols.DATE));
        String headersString = getString(getColumnIndex(EmailDbSchema.EmailTable.Cols.HEADERS));

        EmailModel email = new EmailModel(UUID.fromString(uuidString));
        email.setSender(senderString);
        email.setRecipient(recipientString);
        email.setSubject(subjectString);
        email.setMessage(messageString);
        //TODO change date/headers format
        email.setFullDate(DateFormatHelper.getFullDateFromFullDateString(dateString));
        email.setHeaders(headersString);

        return email;
    }
}
