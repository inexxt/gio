package calendar.scheduling;

import com.gio.calendar.scheduling.SchedulingDetails;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class SchedulingDetailsTest {

    @org.junit.Test
    public void accessFieldsTest() {
        LocalDate date = LocalDate.now();
        SchedulingDetails d = new SchedulingDetails(date,
                date, 1, 2, 3,
                "aa", "bb", "cc");

        assertEquals(d.duration, 1);
        assertEquals(d.startDay, date);
        assertEquals(d.endDay, date);
        assertEquals(d.maximalContinousDuration, 2);
        assertEquals(d.minimalContinousDuration, 3);
        assertEquals(d.eventName, "aa");
        assertEquals(d.eventDescription, "bb");
        assertEquals(d.eventTags, "cc");
    }
}