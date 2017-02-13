package com.iliaskomp.emailapp.InboxActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.mail.Header;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class FormatHelper {
    private static final int MESSAGE_INBOX_LENGTH = 25;

    public static String formatDateForInbox(Date date) {
        String dateString = date.toString();
        SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        try {
            Date newDate=spf.parse(dateString);
            spf= new SimpleDateFormat("dd MMM yyyy");
            dateString = spf.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static String formatMessageShortForInbox(String message) {
        return message.length() <= MESSAGE_INBOX_LENGTH
                ? message : message.substring(0, MESSAGE_INBOX_LENGTH);
    }

    public static HashMap<String, String> getHeaders(Enumeration headers) {
        HashMap<String, String> headersMap = new HashMap<>();
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            headersMap.put(h.getName(), h.getValue());

            System.out.println(h.getName() + ": " + h.getValue());
        }
        return headersMap;
    }
}
