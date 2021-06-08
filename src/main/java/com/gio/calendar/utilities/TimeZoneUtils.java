package com.gio.calendar.utilities;

import java.time.*;

/**
 * Helper class to deal with timezone conversions
 */
public class TimeZoneUtils {
    /**
     * Converts a time (assumed in UTC) to system timezone
     * @param t - time
     * @param d - date
     * @return time converted to system timezone
     */
    public static ZonedDateTime atSystemTimezone(LocalTime t, LocalDate d) {
        LocalDateTime st = d.atTime(t);
        ZonedDateTime zonedStartTime = ZonedDateTime.of(st, ZoneId.of("UTC"));
        return zonedStartTime.withZoneSameInstant(ZoneId.systemDefault());
    }

    /**
     * Converts a time (assumed in system timezone) to UTC
     * @param t - time
     * @param d - date
     * @return time converted to UTC
     */
    public static ZonedDateTime atUTC(LocalTime t, LocalDate d) {
        LocalDateTime st = d.atTime(t);
        ZonedDateTime zonedStartTime = ZonedDateTime.of(st, ZoneId.systemDefault());
        return zonedStartTime.withZoneSameInstant(ZoneId.of("UTC"));
    }
}
