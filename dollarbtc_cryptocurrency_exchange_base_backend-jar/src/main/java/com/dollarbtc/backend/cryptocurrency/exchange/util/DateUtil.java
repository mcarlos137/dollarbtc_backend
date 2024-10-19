/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class DateUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final SimpleDateFormat FORMATTER_SIMPLE = new SimpleDateFormat(DATE_PATTERN, Locale.US);

    public static String getCurrentDate() {
        return FORMATTER_DATE.format(ZonedDateTime.now(ZoneId.of("UTC")));
    }
    
    public static String getMonthStartDate(String date) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of("Greenwich")));
    }

    public static String getDayStartDate(String date) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, 0, 0, 0, 0, ZoneId.of("Greenwich")));
    }
    
    public static String getHourStartDate(String date) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, 0, 0, 0, ZoneId.of("Greenwich")));
    }
    
    public static String getMinuteStartDate(String date) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")));
    }
    
    public static String getDateSecondsBefore(String date, int secondsBefore) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        int second = Integer.parseInt(date.substring(17, 19));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, ZoneId.of("Greenwich")).minus(secondsBefore, ChronoUnit.SECONDS));
    }
    
    public static String getDateMinutesBefore(String date, int minutesBefore) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")).minus(minutesBefore, ChronoUnit.MINUTES));
    }
    
    public static String getDateHoursBefore(String date, int hoursBefore) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, 0, 0, 0, ZoneId.of("Greenwich")).minus(hoursBefore, ChronoUnit.HOURS));
    }
    
    public static String getDateDaysBefore(String date, int daysBefore) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")).minus(daysBefore, ChronoUnit.DAYS));
    }
    
    public static String getDateDaysAfter(String date, int daysAfter) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")).plus(daysAfter, ChronoUnit.DAYS));
    }
    
    public static String getDateHoursAfter(String date, int hoursAfter) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")).plus(hoursAfter, ChronoUnit.HOURS));
    }
    
    public static String getDateMinutesAfter(String date, int minutesAfter) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")).plus(minutesAfter, ChronoUnit.MINUTES));
    }
    
    public static String getDateMonthsBefore(String date, int monthsBefore) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")).minus(monthsBefore, ChronoUnit.MONTHS));
    }
    
    public static String getDateMonthsAfter(String date, int monthsAfter) {
        if (date == null || date.equals("")) {
            date = getCurrentDate();
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(date.substring(11, 13));
        int minute = Integer.parseInt(date.substring(14, 16));
        return FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Greenwich")).plus(monthsAfter, ChronoUnit.MONTHS));
    }
    
    public static String getFileDate(String date) {
        if (date == null) {
            date = getCurrentDate();
        }
        return date.replace(":", "-").replace(".", "--");
    }

    public static String getDate(String fileDate) {
        if (fileDate == null) {
            fileDate = getCurrentDate();
        }
        String firstDatePart = fileDate.substring(0, fileDate.indexOf("T"));
        String secondDatePart = fileDate.substring(fileDate.indexOf("T")).replace("--", ".").replace("-", ":");
        return firstDatePart + secondDatePart;
    }

    public static String getDate(long time) {
        return FORMATTER_DATE.format(ZonedDateTime.ofInstant(new Date(time).toInstant(), ZoneId.of("UTC")));
    }

    public static Date parseDate(String date) {
        try {
            return FORMATTER_SIMPLE.parse(date);
        } catch (ParseException | NumberFormatException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Date parseDate(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        try {
            return FORMATTER_SIMPLE.parse(FORMATTER_DATE.format(ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, ZoneId.of("Greenwich"))));
        } catch (ParseException | NumberFormatException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
