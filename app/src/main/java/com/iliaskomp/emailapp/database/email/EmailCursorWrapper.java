package com.iliaskomp.emailapp.database.email;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.iliaskomp.emailapp.models.EmailModel;
import com.iliaskomp.emailapp.utils.DateFormatHelper;

import java.util.UUID;

/**
 * Converts database objects to java objects for easier access.
 */
public class EmailCursorWrapper extends CursorWrapper {

    /**
     * Instantiates a new Email cursor wrapper.
     *
     * @param cursor the cursor
     */
    public EmailCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Gets email from inbox table.
     *
     * @return the inbox email
     */
    public EmailModel getInboxEmail() {
        String uuidString = getString(getColumnIndex(EmailDbSchema.InboxTable.Cols.UUID));
        String senderString = getString(getColumnIndex(EmailDbSchema.InboxTable.Cols.SENDER));
        String recipientString = getString(getColumnIndex(EmailDbSchema.InboxTable.Cols.RECIPIENT));
        String subjectString = getString(getColumnIndex(EmailDbSchema.InboxTable.Cols.SUBJECT));
        String messageString = getString(getColumnIndex(EmailDbSchema.InboxTable.Cols.MESSAGE));
        String dateString = getString(getColumnIndex(EmailDbSchema.InboxTable.Cols.DATE));
        String headersString = getString(getColumnIndex(EmailDbSchema.InboxTable.Cols.HEADERS));

        EmailModel email = new EmailModel(UUID.fromString(uuidString));
        email.setSender(senderString);
        email.setRecipient(recipientString);
        email.setSubject(subjectString);
        email.setMessage(messageString);
        email.setFullDate(DateFormatHelper.getFullDateFromFullDateString(dateString));
        email.setHeaders(headersString);

        return email;
    }

    /**
     * Gets email from sent table.
     *
     * @return the sent email
     */
    public EmailModel getSentEmail() {
        String uuidString = getString(getColumnIndex(EmailDbSchema.SentTable.Cols.UUID));
        String senderString = getString(getColumnIndex(EmailDbSchema.SentTable.Cols.SENDER));
        String recipientString = getString(getColumnIndex(EmailDbSchema.SentTable.Cols.RECIPIENT));
        String subjectString = getString(getColumnIndex(EmailDbSchema.SentTable.Cols.SUBJECT));
        String messageString = getString(getColumnIndex(EmailDbSchema.SentTable.Cols.MESSAGE));
        String dateString = getString(getColumnIndex(EmailDbSchema.SentTable.Cols.DATE));
        String headersString = getString(getColumnIndex(EmailDbSchema.SentTable.Cols.HEADERS));

        EmailModel email = new EmailModel(UUID.fromString(uuidString));
        email.setSender(senderString);
        email.setRecipient(recipientString);
        email.setSubject(subjectString);
        email.setMessage(messageString);
        email.setFullDate(DateFormatHelper.getFullDateFromFullDateString(dateString));
        email.setHeaders(headersString);

        return email;
    }
}
