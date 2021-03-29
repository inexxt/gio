package com.example.application.utilities.calendar;

import java.util.Calendar;

public class CalendarManager {

    private static final String[] DAY_NAMES = {
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
    };

    private static final String[] MONTHS_NAMES = {
            "January"   ,   "February"  ,   "March"    ,   "April"    ,
            "May"       ,   "June"      ,   "July"     ,   "August"   ,
            "September" ,   "October"   ,   "November" ,   "December"
    };

    /**
     * Returns string name of day of current calendar's date
     * @param targetCalendar calendar object
     * @return String name of the day ("Monday", "Tuesday", ...)
     */
    public static String getCurrentDay(Calendar targetCalendar) {
        int dateDay = targetCalendar.get(Calendar.DAY_OF_WEEK);
        return DAY_NAMES[dateDay - 1];

    }

    /**
     * Returns string name of month of current calendar's date
     * @param targetCalendar calendar object
     * @return String name of the month ("January", "February", ....)
     */
    public static String getCurrentMonth(Calendar targetCalendar) {
        int dateMonth = targetCalendar.get(Calendar.MONTH);
        return MONTHS_NAMES[dateMonth];
    }
    /**
     * Transforms current date of calendar to String in format: "%day, %number_of_day_in_month %month %year"
     * @param targetCalendar calendar object
     * @return String representation of calendar's current date formatted as above
     */
    public static String getCurrentCalendarDate(Calendar targetCalendar) {
        String dayString = getCurrentDay(targetCalendar);
        String monthString = getCurrentMonth(targetCalendar);

        return dayString + ", " + targetCalendar.get(Calendar.DAY_OF_MONTH) + " " + monthString + " " + targetCalendar.get(Calendar.YEAR);
    }
}
