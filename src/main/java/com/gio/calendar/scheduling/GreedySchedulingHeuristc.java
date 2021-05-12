package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GreedySchedulingHeuristc extends SchedulingHeuristic {
    @Override
    public Optional<List<CalendarEvent>> apply(SchedulingDetails details) {
        List<LocalDate> days = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        for (LocalDate i = details.startDay; i.compareTo(details.endDay) < 0; i = i.plusDays(1)) {
            boolean[] blocked = new boolean[24];
            int time = 6;
	    if (i.compareTo(details.startDay)==0)
		time = Math.max(6, LocalTime.now().getHour());
            for (int j = 0; j < time; ++j)
                blocked[j] = true;

            List<CalendarEvent> eventsList;
            try {
                eventsList = CalendarEventRepository.findByDate(i);
            } catch (Exception e) {
                return Optional.empty();
            }
            for (CalendarEvent event : eventsList) {
                for (int j = event.getEventStartTime().getHour(); j < event.getEventEndTime().getHour(); ++j) {
                    blocked[j] = true;
                }
            }

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
        if (details.duration != 0){
            return Optional.empty();
        }
        return Optional.of(CalendarEvent.getRepeatedEvents(days, starts, ends, details.eventName,
                details.eventDescription, details.eventTags));
    }
}
