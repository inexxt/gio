package com.gio.calendar.utilities.calendar.calendarevent;

import com.gio.calendar.utilities.calendar.tag.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarEvent {
    private LocalDate eventDate;

    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    protected List<Tag> eventTag;
    private String eventDescription;
    private String eventName;

    public CalendarEvent(String eventName, String eventDescription, LocalDate eventDate, LocalTime eventStartTime, LocalTime eventEndTime, String tags) {
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTag = Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toList());
        this.eventDescription = eventDescription;
        this.eventName = eventName;
    }

    public CalendarEvent(String eventName, String eventDescription, LocalDate eventDate, LocalTime eventStartTime, LocalTime eventEndTime, ResultSet tags) throws SQLException {
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTag = new ArrayList<Tag>();
        while(tags.next()) {
            eventTag.add(new Tag(tags.getString("tag")));
        }
        this.eventDescription = eventDescription;
        this.eventName = eventName;
    }

    private String timeToString(LocalTime targetTime) {
    	return (targetTime.getHour() < 10 ? "0" + targetTime.getHour() : targetTime.getHour()) + ":" + 
     		   (targetTime.getMinute() < 10 ? "0" + targetTime.getMinute() : targetTime.getMinute());
    }
    
    /* Returns string representation of event start time in format HH:MM
     */
    public String getEventStartTimeString() {
    	return timeToString(eventStartTime);
    }
    
    /* Returns string representation of event end time in format HH:MM
     */
    public String getEventEndTimeString() {
    	return timeToString(eventEndTime);
    }
    
    public String getEventDescription() {
    	return eventDescription;
    }
    
    public String getEventName() {
    	return eventName;
    }
    
    /* For further purposes */
    @Override
    public String toString() {
        return "Date: " + eventDate.toString() +
               "\n" +
                (eventName != null ? "Name: " + eventName + "\n" : "") +
                (eventStartTime != null ? "Start time: " + getEventStartTimeString() + "\n" : "") +
                (eventEndTime   != null ? "End time: " + getEventEndTimeString() + "\n" : "") +
                (eventDescription != null ? "Description: " + eventDescription + "\n" : "") +
               "Tags: ";
    }

    public LocalDateTime getStart() {
        LocalDateTime eventStart = eventStartTime.atDate(eventDate);
        return eventStart;
    }

    public String getEventTags() {
        if (eventTag.isEmpty())
            return "None";
        else {
            StringBuilder sb = new StringBuilder();
            int where = 0;
            for (Tag tag : eventTag) {
                sb.append(tag);
                ++where;
                if (eventTag.size() != where)
                    sb.append(",");
            }
            return sb.toString();
        }
    }
}
