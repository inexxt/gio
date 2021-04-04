package com.gio.calendar.utilities.calendar.calendarevent;

import com.gio.calendar.utilities.calendar.tag.Tag;

import java.time.LocalDate;
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

    public CalendarEvent(LocalDate eDate, LocalTime eStartTime, LocalTime eEndTime, String eTags, String eDesc) {
        eventDate = eDate;
        eventStartTime = eStartTime;
        eventEndTime = eEndTime;

        eventTag = Arrays.stream(eTags.split(",")).map(Tag::new).collect(Collectors.toList());
        eventDescription = eDesc;
    }

    @Override
    public String toString() {
        return "Date: " + eventDate.toString() +
               "\n" +
                (eventStartTime != null ? "Start time: " + eventStartTime.toString() + "\n" : "") +
                (eventEndTime   != null ? "End time: " + eventEndTime.toString() + "\n" : "") +
                (eventDescription != null ? "Description: " + eventDescription + "\n" : "") +
               "Tags: ";
    }
}
