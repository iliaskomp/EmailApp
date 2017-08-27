package com.iliaskomp.emailapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper for formatting date
 */
public class DateFormatHelper {

    /**
     * Gets formattted date string from full date.
     *
     * @param date the date
     * @return the formattted date string from full date
     */
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

    /**
     * Gets formatted date object from formatted date string.
     *
     * @param dateString the formatted date string
     * @return the formatted date object
     */
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

    /**
     * Gets formatted date from full date.
     *
     * @param date the date
     * @return the formatted date from full date
     */
    public static Date getFormattedDateFromFullDate(Date date) {
        String formattedDateString = getFormatttedDateStringFromFullDate(date);
        return getFormattedDateFromFormattedDateString(formattedDateString);
    }

    /**
     * Gets full date from full date string.
     *
     * @param dateString the date string
     * @return the full date from full date string
     */
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
}
