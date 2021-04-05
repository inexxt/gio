package com.gio.calendar.utilities.calendar.calendarevent;

import com.gio.calendar.utilities.calendar.tag.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarEvent {
    private LocalDate eventDate;

    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    private List<Tag> eventTag;
    private String eventDescription;
    private String eventName;

    public CalendarEvent(String eventName, String eventDescription, LocalDate eventDate, LocalTime eventStartTime, LocalTime eventEndTime, String tags) {
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTag = Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toList());;
        this.eventDescription = eventDescription;
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "Date: " + eventDate.toString() +
               "\n" +
                (eventName != null ? "Name: " + eventName + "\n" : "") +
                (eventStartTime != null ? "Start time: " + eventStartTime.toString() + "\n" : "") +
                (eventEndTime   != null ? "End time: " + eventEndTime.toString() + "\n" : "") +
                (eventDescription != null ? "Description: " + eventDescription + "\n" : "") +
               "Tags: ";
    }

    public LocalDateTime getStart() {
        LocalDateTime eventStart = eventStartTime.atDate(eventDate);
        return eventStart;
    }
}
