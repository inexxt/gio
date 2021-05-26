package com.gio.calendar.utilities;

import java.time.LocalTime;

public class TimeDateUtils {
    /* Returns string representation of event start time in format HH:MM */
    public static String timeToString(LocalTime targetTime) {
        return (targetTime.getHour() < 10 ? "0" + targetTime.getHour() : targetTime.getHour()) + ":" +
                (targetTime.getMinute() < 10 ? "0" + targetTime.getMinute() : targetTime.getMinute());
    }
}
