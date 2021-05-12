package calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.gio.calendar.scheduling.SchedulingHeuristic;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;

public class SchedulingHeuristicTest {

    @Test
    public void testGetBlockedSlots() {
        LocalDate day = LocalDate.of(2000, 1, 1);
        LocalTime time = LocalTime.of(14, 0);

        boolean[] shouldBeBlocked = new boolean[24];

        for (int j = 0; j < 6; ++j)
            shouldBeBlocked[j] = true;

        assertArrayEquals(shouldBeBlocked, SchedulingHeuristic.getBlockedSlots(day, false));
    }
}