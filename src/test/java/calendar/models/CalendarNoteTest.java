package calendar.models;

import com.gio.calendar.models.CalendarNote;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class CalendarNoteTest {

    @org.junit.Test
    public void testName() {
        CalendarNote c = new CalendarNote("NAME", "DESC",
                LocalDate.now(),
                "first, second");

        assertEquals("NAME", c.getNoteName());
    }

    @org.junit.Test
    public void testUpdate() {
        CalendarNote c = new CalendarNote("NAME", "DESC",
                LocalDate.now(),
                "first, second");

        CalendarNote c2 = new CalendarNote("NAME2", "DESC2",
                LocalDate.now(),
                "dasdas, vadadsa");
        assertNotEquals(c, c2);
        c.update(c2);
        assertEquals(c, c2);
    }



    @org.junit.Test
    public void testDescription() {
        CalendarNote c = new CalendarNote("NAME", "DESC",
                LocalDate.now(),
                "first, second");

        assertEquals("DESC", c.getNoteDescription());
    }

    @org.junit.Test
    public void testSetId() {
        CalendarNote c = new CalendarNote("NAME", "DESC",
                LocalDate.now(),
                "first, second");

        c.setNoteId(1);

        assertEquals(1, c.getNoteId());
    }

    @org.junit.Test
    public void testSetName() {
        CalendarNote c = new CalendarNote("NAME", "DESC",
                LocalDate.now(),
                "first, second");

        c.setNoteName("NAME1");

        assertEquals("NAME1", c.getNoteName());
    }

}
