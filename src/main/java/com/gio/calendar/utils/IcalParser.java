package com.gio.calendar.utils;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.UidGenerator;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.time.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static net.fortuna.ical4j.util.CompatibilityHints.*;
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY;

public class IcalParser {

    private static void addEvents(List<CalendarEvent> events, PeriodList periodList, PropertyList propertyList) {
        String name, description, tags, location, people;
        name = description = tags = location = people = "";
        try {
            name = propertyList.getProperty("SUMMARY").getValue();
            description = propertyList.getProperty("DESCRIPTION").getValue();
            if (description.length() > 255) {
                description = description.substring(0, 254);
            }
            location = propertyList.getProperty("LOCATION").getValue();
        }
        catch (Exception ignored) {

        }
        finally {

            for (Object po : periodList) {
                Period per = (Period) po;

                LocalTime startTime = Instant.ofEpochMilli(per.getStart().getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
                LocalTime endTime = Instant.ofEpochMilli(per.getEnd().getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
                LocalDate start = Instant.ofEpochMilli(per.getStart().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = Instant.ofEpochMilli(per.getEnd().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

                if (start.compareTo(end) == 0) {
                    events.add(new CalendarEvent(name, description, start, startTime, endTime, tags, location, people));
                } else {
                    events.add(new CalendarEvent(name, description, start, startTime, start.atTime(LocalTime.MAX).toLocalTime(), tags, location, people));

                    for (LocalDate i = start.plusDays(1); i.compareTo(end) != 0; i = i.plusDays(1)) {
                        events.add(new CalendarEvent(name, description, i, i.atStartOfDay().toLocalTime(), i.atTime(LocalTime.MAX).toLocalTime(), tags, location, people));
                    }

                    events.add(new CalendarEvent(name, description, end, end.atStartOfDay().toLocalTime(), endTime, tags, location, people));
                }
            }
        }
    }

    public static List<CalendarEvent> parseFile(InputStream file) throws ParserException, IOException {
        CompatibilityHints.setHintEnabled(KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(KEY_RELAXED_VALIDATION, true);
        CompatibilityHints.setHintEnabled(KEY_OUTLOOK_COMPATIBILITY, true);
        // Reading the file and creating the calendar
        CalendarBuilder builder = new CalendarBuilder();
        Calendar cal = null;
        try {
            cal = builder.build(file);
        }
        catch (ParserException | IOException e) {
            e.printStackTrace();
            throw e;
        }


        // Create the date range which is desired.
        LocalDateTime startDate = LocalDateTime.now().plusYears(-1);
        LocalDateTime endDate = LocalDateTime.now().plusYears(1);
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        DateTime from = new DateTime(startDate.atZone(zoneId).toInstant().toEpochMilli());
        DateTime to = new DateTime(endDate.atZone(zoneId).toInstant().toEpochMilli());

        Period period = new Period(from, to);


        // For each VEVENT in the ICS
        List<CalendarEvent> events = new ArrayList<CalendarEvent>();
        for (Object o : cal.getComponents("VEVENT")) {
            Component c = (Component) o;
            addEvents(events, c.calculateRecurrenceSet(period), c.getProperties());
        }
        return events;
    }

    private static java.util.Calendar getDate(LocalDate date, TimeZone timezone, LocalTime time) {
        java.util.Calendar outDate = new GregorianCalendar();
        outDate.setTimeZone(timezone);
        outDate.set(java.util.Calendar.MONTH, date.getMonthValue() - 1);
        outDate.set(java.util.Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        outDate.set(java.util.Calendar.YEAR, date.getYear());
        outDate.set(java.util.Calendar.HOUR_OF_DAY, time.getHour());
        outDate.set(java.util.Calendar.MINUTE, time.getMinute());
        outDate.set(java.util.Calendar.SECOND, time.getSecond());
        return outDate;
    }


    public static InputStream exportEvents() {
        // Create a calendar
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        // Create a TimeZone
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Europe/Paris");
        VTimeZone tz = timezone.getVTimeZone();

        List<CalendarEvent> events = CalendarEventRepository.findAll();

        for (CalendarEvent event : events) {
            java.util.Calendar startDate = getDate(event.getEventDate(), timezone, event.getEventStartTime());
            java.util.Calendar endDate = getDate(event.getEventDate(), timezone, event.getEventEndTime());

            String eventName = event.getEventName();
            DateTime start = new DateTime(startDate.getTime());
            DateTime end = new DateTime(endDate.getTime());
            VEvent meeting = new VEvent(start, end, eventName);

            // add timezone info..
            meeting.getProperties().add(tz.getTimeZoneId());

            // generate unique identifier..
            Uid uid;
            try {
                UidGenerator ug = new UidGenerator("uidGen");
                uid = ug.generateUid();
            }
            catch(SocketException e) {
                uid = new Uid();
            }
            meeting.getProperties().add(uid);

            meeting.getProperties().add(new Description(event.getEventDescription()));
            meeting.getProperties().add(new Location(event.getEventPlace()));

            icsCalendar.getComponents().add(meeting);

        }

        return new ByteArrayInputStream(icsCalendar.toString().getBytes());
    }
}
