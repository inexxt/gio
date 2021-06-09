package com.gio.calendar.scheduling;

import java.util.*;

import static java.util.Map.entry;

/**
 * Managing class for scheduling heuristics - they have to be registered
 * here, to appear in the list in the UI.
 */
public class SchedulingHeuristicManager {
    static Map<String, SchedulingHeuristic> availableHeuristics = Map.ofEntries(
            entry("Earliest possible", new EarliestPossibleHeuristc()),
            entry("Latest possible", new LatestPossibleHeuristc()),
            entry("Least number of events", new LeastNumberOfEventsHeuristic())
    );

    /**
     * Gets all heuristic names, in a human-readable format (to be displayed in the UI)
     * @return list of heuristcs names
     */
    public static Collection<String> getAvailableHeuristicNames() {
        return availableHeuristics.keySet();
    }

    /**
     * Getter for a heuristic
     * @param name - human-readable name from the UI
     * @return SchedulingHeuristic
     */
    public static SchedulingHeuristic getHeuristicByName(String name) {
        return availableHeuristics.get(name);
    }
}
