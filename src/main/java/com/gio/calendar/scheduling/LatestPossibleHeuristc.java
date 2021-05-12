package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LatestPossibleHeuristc extends SchedulingHeuristic {
    @Override
    public List<CalendarEvent> apply(SchedulingDetails details) {
        List<LocalDate> days = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        for (LocalDate i = details.endDay; i.compareTo(details.startDay) != 0; i = i.minusDays(1)) {
            boolean[] blocked = getBlockedSlots(i, i.compareTo(details.startDay) == 0);

            for (int j = Math.min(details.duration, details.maximalContinousDuration);
                 j >= Math.min(details.duration, details.minimalContinousDuration); --j) {
                boolean ok = false;
                for (int x = 23 - j; x >= 6; x--) {
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
        if (details.duration != 0){
            return Collections.emptyList();
        }
        return CalendarEvent.getRepeatedEvents(days, starts, ends, details.eventName,
                details.eventDescription, details.eventTags);
    }

}
