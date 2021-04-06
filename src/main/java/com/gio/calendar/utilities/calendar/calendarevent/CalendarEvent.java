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

    private List<Tag> eventTags;
    private String eventDescription;
    private String eventName;

    public CalendarEvent(String eventName, 
    					 String eventDescription, 
    					 LocalDate eventDate, 
    					 LocalTime eventStartTime, 
    					 LocalTime eventEndTime, 
    					 String tags) {

        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTags = Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toList());;
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
        return eventStartTime.atDate(eventDate);
    }

    public List<Tag> getEventTags() {
        return eventTags;
    }
}
