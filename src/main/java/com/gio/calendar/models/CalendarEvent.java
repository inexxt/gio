package com.gio.calendar.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name="CalendarEvent")
@NamedQueries({
@NamedQuery(name = "CalendarEvent.findById",
        query = "SELECT e FROM CalendarEvent e WHERE e.eventId = :eventId"),
@NamedQuery(name = "CalendarEvent.findAll",
        query = "SELECT e FROM CalendarEvent e"),
@NamedQuery(name = "CalendarEvent.findByTime",
        query = "SELECT e FROM CalendarEvent e WHERE (e.eventStartTime > :startTime) and (e.eventEndTime < :endTime)"),
@NamedQuery(name = "CalendarEvent.findByDate",
        query = "SELECT e FROM CalendarEvent e WHERE e.eventDate = :date")
})
public class CalendarEvent {
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public LocalTime getEventEndTime() {
        return eventEndTime;
    }

    public Set<Tag> getEventTags() {
        return eventTags;
    }

    public Set<Person> getEventPeople() {
        return eventPeople;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPlace() {
        return eventPlace;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int eventId; // Can't be private because persistance needs it
    private LocalDate eventDate;

    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "Event_Tag",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    private Set<Tag> eventTags;


    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "Event_Person",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "personName")
    )
    private Set<Person> eventPeople;

    private String eventDescription;
    private String eventName;
    private String eventPlace;

    public CalendarEvent() { } // for persistance

    public CalendarEvent(String eventName,
    					 String eventDescription, 
    					 LocalDate eventDate, 
    					 LocalTime eventStartTime, 
    					 LocalTime eventEndTime, 
    					 String tags,
                         String place,
                         String people) {
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTags = Tag.tagsFromString(tags);
        this.eventDescription = eventDescription;
        this.eventName = eventName;
        this.eventPeople = Person.peopleFromString(people);
        this.eventPlace = place;
    }

    /* Returns string representation of event start time in format HH:MM */
    private String timeToString(LocalTime targetTime) {
    	return (targetTime.getHour() < 10 ? "0" + targetTime.getHour() : targetTime.getHour()) + ":" + 
     		   (targetTime.getMinute() < 10 ? "0" + targetTime.getMinute() : targetTime.getMinute());
    }

    public String getEventStartTimeString() {
    	return timeToString(eventStartTime);
    }
    public String getEventEndTimeString() {
        return timeToString(eventEndTime);
    }

    public void update(CalendarEvent newEvent) {
        this.eventDate = newEvent.getEventDate();
        this.eventStartTime = newEvent.getEventStartTime();
        this.eventEndTime = newEvent.getEventEndTime();
        this.eventTags = newEvent.getEventTags();
        this.eventDescription = newEvent.getEventDescription();
        this.eventName = newEvent.getEventName();
        this.eventPeople = newEvent.getEventPeople();
        this.eventPlace = newEvent.getEventPlace();
    }
}
