package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class SchedulingHeuristicManager {
    static Map<String, SchedulingHeuristic> availableHeuristics = Map.ofEntries(
            entry(GreedySchedulingHeuristc.class.getName(), new GreedySchedulingHeuristc()),
            entry(EvenlyDistributedSchedulingHeuristc.class.getName(), new EvenlyDistributedSchedulingHeuristc())
        );

    public static Collection<String> getAvailableHeuristicNames() {
        return availableHeuristics.keySet();
    }

    public static SchedulingHeuristic getHeuristicByName(String name) {
        return availableHeuristics.get(name);
    }
}
