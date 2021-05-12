package calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.scheduling.EarliestPossibleHeuristc;
import com.gio.calendar.scheduling.SchedulingDetails;
import com.gio.calendar.scheduling.SchedulingHeuristic;
import org.junit.Test;

import java.time.*;
import java.util.*;

import static org.junit.Assert.*;

public class EarliestPossibleHeuristcTest {

    @Test
    public void applyTest() {
        LocalDate day = LocalDate.of(2000, 1, 1);
        LocalTime time = LocalTime.of(6, 0);

        List<CalendarEvent> events = Collections.singletonList(new CalendarEvent("aa", "bb",
                day, time, time.plusHours(3), "cc", "", ""));
        SchedulingDetails details = new SchedulingDetails(day,
                day.plusDays(1), 3, 3, 2,
                "aa", "bb", "cc");

        SchedulingHeuristic heuristic = new EarliestPossibleHeuristc();
        Instant instant = day.atStartOfDay(ZoneId.systemDefault()).plusHours(6).toInstant();
        heuristic.setClock(Clock.fixed(instant, ZoneId.systemDefault()));
        assertEquals(events, heuristic.apply(details));
    }
}