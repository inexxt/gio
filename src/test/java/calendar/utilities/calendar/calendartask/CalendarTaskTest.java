package calendar.utilities.calendar.calendartask;

import com.gio.calendar.utilities.calendar.calendarevent.CalendarEvent;
import com.gio.calendar.utilities.calendar.calendartask.CalendarTask;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class CalendarTaskTest {

    @org.junit.Test
    public void testDuration() {
        CalendarTask c = new CalendarTask("NAME", "DESC",
                LocalDate.now(),
                "first, second",
                5);

        assertEquals(5, c.getDuration());
    }

    @org.junit.Test
    public void testDescription() {
        CalendarTask c = new CalendarTask("NAME", "DESC",
                LocalDate.now(),
                "first, second",
                5);

        assertEquals("DESC", c.getTaskDescription());
    }

    @org.junit.Test
    public void testName() {
        CalendarTask c = new CalendarTask("NAME", "DESC",
                LocalDate.now(),
                "first, second",
                5);

        assertEquals("NAME", c.getTaskName());
    }

    @org.junit.Test
    public void testToString() {
        LocalDate now = LocalDate.now();
        CalendarTask c = new CalendarTask("NAME", "DESC",
                now,
                "first, second",
                5);

        System.out.println(c.toString());
        assertEquals("Date: " + now.toString() +
                "\n" +
                "Name: " + "NAME" + "\n" +
                "Description: " + "DESC" + "\n" +
                "Tags: " + "first, second", c.toString());
    }
}
