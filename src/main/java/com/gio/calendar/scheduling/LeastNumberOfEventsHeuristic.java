package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A heuristic that tries to schedule task to be done such that it consists
 * of as few events as possible.
 */
public class LeastNumberOfEventsHeuristic extends SchedulingHeuristic {

    /**
     * Apply the heuristic to try to schedule task events in such a way that
     * it consists of as few events as possible (e.g. is divided into as few
     * sessions as possible).
     * @param details of the task
     * @return list of CalendarEvent to be scheduled (empty if not successful)
     */
    @Override
    public List<CalendarEvent> apply(SchedulingDetails details) {
        List<List<CalendarEvent>> repetitions = new ArrayList<>();
        for (int event_length = details.maximalContinousDuration; event_length >= details.minimalContinousDuration; --event_length) {
            int duration_left = details.duration;

            List<LocalDate> days = new ArrayList<>();
            List<Integer> starts = new ArrayList<>();
            List<Integer> ends = new ArrayList<>();

            for (LocalDate i = details.endDay.minusDays(1); i.compareTo(details.startDay) >= 0; i = i.minusDays(1)) {
                boolean[] blocked = getBlockedSlots(i, i.compareTo(details.startDay) == 0);

                boolean ok = false;
                for (int x = 6; x < 24 - event_length; x++) {
                    for (int y = x; y < x + event_length; ++y) {
                        if (blocked[y])
                            break;
                        ok = (y + 1 == x + event_length);
                    }
                    if (ok) {
                        starts.add(x);
                        ends.add(x + event_length);
                        duration_left -= event_length;
                        days.add(i);
                        break;
                    }
                }
                if (ok)
                    break;
            }

            if (duration_left == 0) {
                repetitions.add(CalendarEvent.getRepeatedEvents(days, starts, ends, details.eventName,
                        details.eventDescription, details.eventTags));
            }
        }

        return repetitions.stream().min(Comparator.comparing(List::size)).orElse(Collections.emptyList());
    }
}
