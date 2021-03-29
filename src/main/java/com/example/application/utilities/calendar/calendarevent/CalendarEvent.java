package com.example.application.utilities.calendar.calendarevent;

import com.example.application.utilities.calendar.tag.Tag;

public class CalendarEvent {
    private int day;
    private int month;
    private int year;

    private Tag eventTag;
    private String eventDescription;

    public CalendarEvent(int d, int m, int y, Tag et, String desc) {
        day = d;
        month = m;
        year = y;
        eventTag = et;
        eventDescription = desc;
    }

    @Override
    public String toString() {
        return "TO DO: EVENT TO STRING\n";
    }
}
