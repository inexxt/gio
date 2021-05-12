package calendar.scheduling;

import com.gio.calendar.scheduling.EarliestPossibleHeuristc;
import com.gio.calendar.scheduling.LatestPossibleHeuristc;
import com.gio.calendar.scheduling.LeastNumberOfEventsHeuristic;
import com.gio.calendar.scheduling.SchedulingHeuristicManager;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class SchedulingHeuristicManagerTest {

    @Test
    public void getAvailableHeuristicNames() {
        Collection<String> first = Arrays.asList("EarliestPossibleHeuristc","LatestPossibleHeuristc","LeastNumberOfEventsHeuristic");
        Collection<String> second = SchedulingHeuristicManager.getAvailableHeuristicNames();
        assertTrue(first.size() == second.size() && first.containsAll(second) && second.containsAll(first));
    }
}