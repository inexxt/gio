package com.gio.calendar.utilities;

import java.time.*;

public class TimeZoneUtils {
    public static ZonedDateTime atSystemTimezone(LocalTime t, LocalDate d) {
        LocalDateTime st = d.atTime(t);
        ZonedDateTime zonedStartTime = ZonedDateTime.of(st, ZoneId.of("UTC"));
        return zonedStartTime.withZoneSameInstant(ZoneId.systemDefault());
    }

    public static ZonedDateTime atUTC(LocalTime t, LocalDate d) {
        LocalDateTime st = d.atTime(t);
        ZonedDateTime zonedStartTime = ZonedDateTime.of(st, ZoneId.systemDefault());
        return zonedStartTime.withZoneSameInstant(ZoneId.of("UTC"));
    }
}
