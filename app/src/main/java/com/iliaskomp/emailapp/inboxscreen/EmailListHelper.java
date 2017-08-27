package com.iliaskomp.emailapp.inboxscreen;

/**
 * Helper for email list activity.
 */
public class EmailListHelper {
    /**
     * Gets a string as an argument and returns the trimmed first line of that string
     * Used for displaying the first line of the message in the email list.
     * @param message the message
     * @return the string
     */
//
    public static String formatShortMessageForEmailList(String message) {
        if (message != null) {
            return message.trim().split("\r\n|\r|\n", 2)[0];
        }
        return "";
    }
}
