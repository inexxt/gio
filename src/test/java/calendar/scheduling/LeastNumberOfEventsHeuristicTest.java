package calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.scheduling.EarliestPossibleHeuristc;
import com.gio.calendar.scheduling.LeastNumberOfEventsHeuristic;
import com.gio.calendar.scheduling.SchedulingDetails;
import com.gio.calendar.scheduling.SchedulingHeuristic;
import org.junit.Test;

import java.time.*;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LeastNumberOfEventsHeuristicTest {

    @Test
    public void applyTest() {
        LocalDate day = LocalDate.of(2000, 1, 1);
        LocalTime time = LocalTime.of(6, 0);

        List<CalendarEvent> events = Collections.singletonList(new CalendarEvent("aa", "bb",
                day.plusDays(1), time, time.plusHours(17), "cc", "", ""));
        SchedulingDetails details = new SchedulingDetails(day,
                day.plusDays(2), 17, 17, 2,
                "aa", "bb", "cc");

        SchedulingHeuristic heuristic = new LeastNumberOfEventsHeuristic();
        Instant instant = day.atStartOfDay(ZoneId.systemDefault()).plusHours(12).toInstant();
        heuristic.setClock(Clock.fixed(instant, ZoneId.systemDefault()));
        assertEquals(events, heuristic.apply(details));
    }
}