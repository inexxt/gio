package calendar.utilities;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.utilities.IcalParser;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

public class IcalParserTest {

    private java.util.Calendar getDate(int month, int day, int year, int hour, int minute, int second, TimeZone timezone) {
        java.util.Calendar date = new GregorianCalendar();
        date.setTimeZone(timezone);
        date.set(java.util.Calendar.MONTH, month);
        date.set(java.util.Calendar.DAY_OF_MONTH, day);
        date.set(java.util.Calendar.YEAR, year);
        date.set(java.util.Calendar.HOUR_OF_DAY, hour);
        date.set(java.util.Calendar.MINUTE, minute);
        date.set(java.util.Calendar.SECOND, second);
        return date;
    }

    private net.fortuna.ical4j.model.Calendar getCalendar() {
        // Create a TimeZone
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Europe/Paris");

        // Start Date is on: April 1, currentYear, 9:00 am
        java.util.Calendar startDate = getDate(java.util.Calendar.APRIL, 1, LocalDate.now().getYear(), 9, 0, 0, timezone);

        // End Date is on: April 1, currentYear, 13:00
        java.util.Calendar endDate = getDate(Calendar.APRIL, 1, LocalDate.now().getYear(), 13, 0, 0, timezone);

        // Create the event
        String eventName = "Progress Meeting";
        DateTime start = new DateTime(startDate.getTime());
        DateTime end = new DateTime(endDate.getTime());
        VEvent meeting = new VEvent(start, end, eventName);
        meeting.getProperties().add(new Description("description"));


        // Create a calendar
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        // Add the event and print
        icsCalendar.getComponents().add(meeting);
        return icsCalendar;
    }

    @org.junit.Test
    public void testFileParser() {
        net.fortuna.ical4j.model.Calendar icsCalendar = getCalendar();
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), 4, 1);
        LocalTime timeStart = LocalTime.of(9, 0, 0);
        LocalTime endTime = LocalTime.of(13, 0, 0);
        CalendarEvent calendarEvent = new CalendarEvent("Progress Meeting", "description", date, timeStart, endTime, "", "", "");

        try {
            List<CalendarEvent> events = IcalParser.parseFile(new ByteArrayInputStream(icsCalendar.toString().getBytes()));
            assertEquals(1, events.size());
            assertTrue(calendarEvent.compareWithoutId(events.get(0)));
        }
        catch (Exception e) {
            fail();
        }
    }

    @org.junit.Test
    public void testExport() {
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), 4, 1);
        LocalTime timeStart = LocalTime.of(9, 0, 0);
        LocalTime endTime = LocalTime.of(13, 0, 0);
        CalendarEvent calendarEvent = new CalendarEvent("Progress Meeting", "description", date, timeStart, endTime, "", "", "");
        List<CalendarEvent> events = new ArrayList<CalendarEvent>();
        events.add(calendarEvent);
        InputStream input = IcalParser.exportEvents(events);
        try {
            List<CalendarEvent> exported = IcalParser.parseFile(input);
            assertEquals(1, exported.size());
            assertTrue(calendarEvent.compareWithoutId(exported.get(0)));
        }
        catch (Exception e) {
            fail();
        }
    }
}
