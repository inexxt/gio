package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

public abstract class SchedulingHeuristic implements Function<SchedulingDetails, List<CalendarEvent>> {

    private Clock clock;

    public boolean[] getBlockedSlots(LocalDate day, boolean is_start_day) {
        boolean[] blocked = new boolean[24];
        int time = 6;
        if (is_start_day)
            time = Math.max(6, LocalTime.now(clock).getHour());
        for (int j = 0; j < time; ++j)
            blocked[j] = true;

        List<CalendarEvent> eventsList = CalendarEventRepository.findByDate(day);
        for (CalendarEvent event : eventsList) {
            for (int j = event.getEventStartTime().getHour(); j < event.getEventEndTime().getHour(); ++j) {
                blocked[j] = true;
            }
        }
        return blocked;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public Clock getClock() {
        return clock;
    }

    public SchedulingHeuristic() {
        clock = Clock.system(TimeZone.getDefault().toZoneId());
    }
}
