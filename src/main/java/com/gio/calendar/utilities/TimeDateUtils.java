package com.gio.calendar.utilities;

import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Utils class for time and date
 */
public class TimeDateUtils {
    /**
     * Returns string representation of event start time in format HH:MM
     * @param targetTime
     * @return  string representation of event start time in format HH:MM
     */
    public static String timeToString(LocalTime targetTime) {
        return (targetTime.getHour() < 10 ? "0" + targetTime.getHour() : targetTime.getHour()) + ":" +
                (targetTime.getMinute() < 10 ? "0" + targetTime.getMinute() : targetTime.getMinute());
    }

    /**
     * Returns string representation of targetTime for specific zone
     * @param targetTime
     * @return string representation of targetTime for specific zone
     */
    public static String zonedTimeToString(ZonedDateTime targetTime) {
        return (targetTime.getHour() < 10 ? "0" + targetTime.getHour() : targetTime.getHour()) + ":" +
                (targetTime.getMinute() < 10 ? "0" + targetTime.getMinute() : targetTime.getMinute());
    }

}
