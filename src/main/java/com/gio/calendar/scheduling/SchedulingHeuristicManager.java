package com.gio.calendar.scheduling;

import java.util.*;

import static java.util.Map.entry;

public class SchedulingHeuristicManager {
    static Map<String, SchedulingHeuristic> availableHeuristics = Map.ofEntries(
            entry("Earliest possible", new EarliestPossibleHeuristc()),
            entry("Latest possible", new LatestPossibleHeuristc()),
            entry("Least number of events", new LeastNumberOfEventsHeuristic())
    );

    public static Collection<String> getAvailableHeuristicNames() {
        return availableHeuristics.keySet();
    }

    public static SchedulingHeuristic getHeuristicByName(String name) {
        return availableHeuristics.get(name);
    }
}
