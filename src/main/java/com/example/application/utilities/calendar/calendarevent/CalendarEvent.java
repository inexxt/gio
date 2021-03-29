package com.example.application.utilities.calendar.calendarevent;

import com.example.application.utilities.calendar.tag.Tag;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarEvent {
    private LocalDate eventDate;

    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    private Tag eventTag;
    private String eventDescription;

    public CalendarEvent(LocalDate eDate, LocalTime eStartTime, LocalTime eEndTime, Tag eTags, String eDesc) {
        eventDate = eDate;
        eventStartTime = eStartTime;
        eventEndTime = eEndTime;

        eventTag = eTags;
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
