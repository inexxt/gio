package com.gio.calendar.utils;

import com.gio.calendar.models.CalendarEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static net.fortuna.ical4j.util.CompatibilityHints.*;
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY;

public class IcalParser {

    private static void addEvents(List<CalendarEvent> events, PeriodList periodList, PropertyList propertyList) {
        String name = propertyList.getProperty("SUMMARY").getValue();
        String description = propertyList.getProperty("DESCRIPTION").getValue();
        if (description.length() > 255) {
            description = description.substring(0, 254);
        }
        String tags = "";
        String location = propertyList.getProperty("LOCATION").getValue();
        String people = "";

        for (Object po : periodList) {
            Period per = (Period) po;

            LocalTime startTime = Instant.ofEpochMilli(per.getStart().getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime endTime = Instant.ofEpochMilli(per.getEnd().getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDate start = Instant.ofEpochMilli(per.getStart().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = Instant.ofEpochMilli(per.getEnd().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

            if (start.compareTo(end) == 0) {
                events.add(new CalendarEvent(name, description, start, startTime, endTime, tags, location, people));
            }
            else {
                events.add(new CalendarEvent(name, description, start, startTime, start.atTime(LocalTime.MAX).toLocalTime(), tags, location, people));

                for (LocalDate i = start.plusDays(1); i.compareTo(end) != 0; i = i.plusDays(1)) {
                    events.add(new CalendarEvent(name, description, i, i.atStartOfDay().toLocalTime(), i.atTime(LocalTime.MAX).toLocalTime(), tags, location, people));
                }

                events.add(new CalendarEvent(name, description, end, end.atStartOfDay().toLocalTime(), endTime, tags, location, people));
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
}
