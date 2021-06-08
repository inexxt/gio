package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

/**
 * Abstract class for herusitcs scheduling task events.
 */
public abstract class SchedulingHeuristic implements Function<SchedulingDetails, List<CalendarEvent>> {

    private Clock clock;

    /**
     * A helper function that lists the slots that are blocked during given day.
     * In particular, we don't want to schedule events before 6am.
     * @param day
     * @param is_start_day - is this the current day? if yes, don't schedule
     *                       anything in the past (i.e. before current time).
     * @return blocked slots 24-element array in a 1-hour granularity
     *         (true if blocked)
     */
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

    /**
     * Helper method to set a clock, useful for mocking.
     * @param clock
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * Getter for the clock
     * @return clock
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Constructor for the SchedulingHeuristic - sets the clock to be the
     * system's default.
     */
    public SchedulingHeuristic() {
        clock = Clock.system(TimeZone.getDefault().toZoneId());
    }
}
