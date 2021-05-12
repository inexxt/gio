package com.gio.calendar.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CalendarEvent")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;
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

    public CalendarEvent() {
    } // for persistance

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarEvent that = (CalendarEvent) o;
        return eventId == that.eventId &&
                Objects.equals(eventDate, that.eventDate) &&
                Objects.equals(eventStartTime, that.eventStartTime) &&
                Objects.equals(eventEndTime, that.eventEndTime) &&
                Objects.equals(eventTags, that.eventTags) &&
                Objects.equals(eventPeople, that.eventPeople) &&
                Objects.equals(eventDescription, that.eventDescription) &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(eventPlace, that.eventPlace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventDate, eventStartTime, eventEndTime, eventTags, eventPeople, eventDescription, eventName, eventPlace);
    }

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

    public static List<CalendarEvent> getRepeatedEvents(List<LocalDate> days,
                                                        List<Integer> starts,
                                                        List<Integer> ends,
                                                        String eventName,
                                                        String eventDescription,
                                                        String eventTags) {
        List<CalendarEvent> events = new ArrayList<>();
        for (int i = 0; i < days.size(); ++i) {
            events.add(new CalendarEvent(
                    eventName,
                    eventDescription,
                    days.get(i),
                    LocalTime.of(starts.get(i), 0),
                    LocalTime.of(ends.get(i), 0),
                    eventTags,
                    "",
                    ""));
        }
        return events;
    }

    public boolean compareWithoutId(CalendarEvent event) {
        return eventDate.equals(event.getEventDate()) &&
                eventStartTime.equals(event.getEventStartTime()) &&
                eventEndTime.equals(event.getEventEndTime()) &&
                eventTags.equals(event.getEventTags()) &&
                eventPeople.equals(event.getEventPeople()) &&
                eventDescription.equals(event.getEventDescription()) &&
                eventName.equals(event.getEventName()) &&
                eventPlace.equals(event.getEventPlace());
    }

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

}
