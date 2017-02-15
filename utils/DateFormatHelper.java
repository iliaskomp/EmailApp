package com.iliaskomp.emailapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by IliasKomp on 13/02/17.
 */

public class DateFormatHelper {

    public static String getFormatttedDateStringFromFullDate(Date date) {
        String dateString = date.toString();
        SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        try {
            Date newDate = spf.parse(dateString);
            spf = new SimpleDateFormat("dd MMM yyyy");
            dateString = spf.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static Date getFormattedDateFromFormattedDateString(String dateString) {
        DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getFormattedDateFromFullDate(Date date) {
        String formattedDateString = getFormatttedDateStringFromFullDate(date);
        return getFormattedDateFromFormattedDateString(formattedDateString);
    }

    public static Date getFullDateFromFullDateString(String dateString) {
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // format date from full date
    // xxx format date from format date string
    // format date string from format date
    // xxx format date string from full date
    // xxx full date from full date string

}
