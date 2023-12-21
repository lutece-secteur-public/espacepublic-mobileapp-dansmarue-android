package com.accenture.dansmarue.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtils
 * Created by PK on 15/05/2017.
 */
public class DateUtils {

    public static SimpleDateFormat DATE_FORMAT_SIRA = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat DATE_FORMAT_RAMEN = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static SimpleDateFormat DATE_FORMAT_SIRA_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat DATE_FORMAT_RAMEN_DATE = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat DATE_FORMAT_SIRA_TIME = new SimpleDateFormat("HH:mm");

    private DateUtils() {
        // Avoid instantiation of the class
    }

    public static Date parse(final String date, SimpleDateFormat format) {
        if (date == null) {
            return null;
        }

        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parse(final String date) {
        return DateUtils.parse(date, DATE_FORMAT_SIRA);
    }

    public static String format(final Date date) {
        if (date == null) {
            return null;
        }

        return DateUtils.format(date, DATE_FORMAT_SIRA);
    }

    public static String format(final Date date, SimpleDateFormat format) {
        if (date == null) {
            return null;
        }

        return format.format(date);
    }

    public static String extractDate(Date date) {
        return DateUtils.extractDate(date, DATE_FORMAT_SIRA_DATE);
    }

    public static String extractDate(Date date, SimpleDateFormat format) {
        return DateUtils.format(date, format);
    }

    public static String extractTime(Date date) {
        return DateUtils.format(date, DATE_FORMAT_SIRA_TIME);
    }
}
