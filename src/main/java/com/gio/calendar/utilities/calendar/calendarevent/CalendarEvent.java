package com.gio.calendar.utilities.calendar.calendarevent;

import com.gio.calendar.utilities.calendar.person.Person;
import com.gio.calendar.utilities.calendar.tag.Tag;
import org.apache.commons.lang3.StringUtils;

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
	private int eventId;
    private LocalDate eventDate;

    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    private List<Tag> eventTags;
    private List<Person> eventPeople;

    private String eventDescription;
    private String eventName;
    private String eventPlace;

    public CalendarEvent(int eventId,
    					 String eventName, 
    					 String eventDescription, 
    					 LocalDate eventDate, 
    					 LocalTime eventStartTime, 
    					 LocalTime eventEndTime, 
    					 String tags,
                         String place,
                         String people) {
    	
    	this.eventId = eventId;
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTags = Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toList());
        this.eventDescription = eventDescription;
        this.eventName = eventName;
        this.eventPeople = Arrays.stream(people.split(",")).map(Person::new).collect(Collectors.toList());
        this.eventPlace = place;
    }

    public CalendarEvent(int eventId,
    					 String eventName, 
    					 String eventDescription, 
    					 LocalDate eventDate, 
    					 LocalTime eventStartTime, 
    					 LocalTime eventEndTime, 
    					 ResultSet tags,
                         String place,
                         ResultSet people) throws SQLException {
    	
    	this.eventId = eventId;
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTags = new ArrayList<Tag>();
        while(tags.next()) {
            eventTags.add(new Tag(tags.getString("tag")));
        }
        this.eventDescription = eventDescription;
        this.eventName = eventName;
        this.eventPlace = place;
        this.eventPeople = new ArrayList<Person>();
        while(people.next()) {
            eventPeople.add(new Person(people.getString("person")));
        }
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

    public String getEventPlace() {
        return eventPlace;
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
                (eventPlace != null ? "Event place: " + eventPlace + "\n" : "") +
                "Tags: " + eventTags + "\n" +
                "People: " + eventPeople;
    }

    public LocalDateTime getStart() {
        return eventStartTime.atDate(eventDate);
    }

    public String getEventTags() {
        if (eventTags.isEmpty()) {
            return "None";
        }
        List<String> ss = eventTags.stream().map(Tag::getTagName).collect(Collectors.toList());
        return String.join(",", ss);
    }

    public String getEventPeople() {
        if (eventPeople.isEmpty()) {
            return "None";
        }
        List<String> ss = eventPeople.stream().map(Person::getPersonDetails).collect(Collectors.toList());
        return String.join(",", ss);
    }


    public int getEventId() {
    	return eventId;
    }
}
