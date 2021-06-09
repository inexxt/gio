package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A heuristic that tries to schedule task to be done as soon as possible
 */
public class EarliestPossibleHeuristc extends SchedulingHeuristic {

    /**
     * Apply the heuristic to try to schedule task events as soon as possible
     * @param details of the task
     * @return list of CalendarEvent to be scheduled (empty if not successful)
     */
    @Override
    public List<CalendarEvent> apply(SchedulingDetails details) {
        List<LocalDate> days = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        for (LocalDate i = details.startDay; i.compareTo(details.endDay) < 0; i = i.plusDays(1)) {
            boolean[] blocked = getBlockedSlots(i, i.compareTo(details.startDay) == 0);

            for (int j = Math.min(details.duration, details.maximalContinousDuration);
                 j >= Math.min(details.duration, details.minimalContinousDuration); --j) {
                boolean ok = false;
                for (int x = 6; x < 24 - j; ++x) {
                    for (int y = x; y < x + j; ++y) {
                        if (blocked[y])
                            break;
                        ok = (y + 1 == x + j);
                    }
                    if (ok) {
                        starts.add(x);
                        ends.add(x + j);
                        details.duration -= j;
                        days.add(i);
                        break;
                    }
                }
                if (ok)
                    break;
            }
        }
        if (details.duration != 0) {
            return Collections.emptyList();
        }
        return CalendarEvent.getRepeatedEvents(days, starts, ends, details.eventName,
                details.eventDescription, details.eventTags);
    }
}
