package com.gio.calendar.scheduling;

import java.util.*;

import static java.util.Map.entry;

public class SchedulingHeuristicManager {
    static Map<String, SchedulingHeuristic> availableHeuristics = Map.ofEntries(
            entry(EarliestPossibleHeuristc.class.getName(), new EarliestPossibleHeuristc()),
            entry(LatestPossibleHeuristc.class.getName(), new LatestPossibleHeuristc()),
            entry(LeastNumberOfEventsHeuristic.class.getName(), new LeastNumberOfEventsHeuristic())
        );

    public static Collection<String> getAvailableHeuristicNames() {
        return availableHeuristics.keySet();
    }

    public static SchedulingHeuristic getHeuristicByName(String name) {
        return availableHeuristics.get(name);
    }
}
