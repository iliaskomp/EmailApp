package com.iliaskomp.emailapp.inboxscreen;

/**
 * Created by IliasKomp on 15/02/17.
 */

public class EmailListHelper {
    //Gets a string and returns the trimmed first line of that string
    public static String formatShortMessageForEmailList(String message) {
        if (message != null) {
            return message.trim().split("\r\n|\r|\n", 2)[0];
        }
        return "";
    }
}
