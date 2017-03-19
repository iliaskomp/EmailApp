package com.iliaskomp.emailapp.inboxscreen;

/**
 * Created by IliasKomp on 15/02/17.
 */

public class EmailListHelper {
    private static final int MESSAGE_INBOX_LENGTH = 45;

    public static String formatShortMessageForEmailList(String message) {
        return message.trim().length() <= MESSAGE_INBOX_LENGTH
                ? message.trim() : message.substring(0, MESSAGE_INBOX_LENGTH).trim();
    }
}
