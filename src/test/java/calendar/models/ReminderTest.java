package calendar.models;

import com.gio.calendar.models.Reminder;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class ReminderTest {
    @org.junit.Test
    public void testDate() {
        LocalDate reminderDate = LocalDate.now();

        Reminder r = new Reminder(reminderDate, LocalTime.now(), "example content");

        assertEquals(reminderDate, r.getReminderDate());
    }

    @org.junit.Test
    public void testTime() {
        LocalTime reminderTime = LocalTime.now();

        Reminder r = new Reminder(LocalDate.now(), reminderTime, "reminder content");

        assertEquals(reminderTime, r.getReminderTime());
    }
}
