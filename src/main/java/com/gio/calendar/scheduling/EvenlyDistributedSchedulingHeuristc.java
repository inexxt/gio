package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvenlyDistributedSchedulingHeuristc extends SchedulingHeuristic {
    @Override
    public Optional<List<CalendarEvent>> apply(SchedulingDetails schedulingDetails) {
        return Optional.empty();
    }
}
