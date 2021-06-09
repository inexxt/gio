package com.gio.calendar.models;

import com.gio.calendar.utilities.TimeDateUtils;

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

/**
 * Represents calendar event
 */
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

    /**
     * Constructor for persistance.
     */
    public CalendarEvent() {
    }

    /**
     * Returns true if o equals this object and false otherwise.
     * @param o
     * @return true if o equals this object and false otherwise.
     */
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

    /**
     * Generates hash code of object.
     * @return Integer representing hash code of object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventDate, eventStartTime, eventEndTime, eventTags, eventPeople, eventDescription, eventName, eventPlace);
    }

    /**
     * Standard constructor of this class
     * @param eventName
     * @param eventDescription
     * @param eventDate
     * @param eventStartTime
     * @param eventEndTime
     * @param tags
     * @param place
     * @param people
     */
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

    /**
     * Gets event start time
     * @return String representing event start time
     */
    public String getEventStartTimeString() {
        return TimeDateUtils.timeToString(eventStartTime);
    }

    /**
     * Gets event end time
     * @return String representing event end time
     */
    public String getEventEndTimeString() {
        return TimeDateUtils.timeToString(eventEndTime);
    }

    /**
     * Updates object's fields with values from newEvent.
     * @param newEvent
     */
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

    /**
     * Generates list of repeated events. It computes one event for each day in days list.
     * @param days
     * @param starts
     * @param ends
     * @param eventName
     * @param eventDescription
     * @param eventTags
     * @return  List of events
     */
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

    /**
     * Compares event with this object without comparing id.
     * @param event
     * @return True if all fields (except id) of event equals all fields of object and false otherwise.
     */
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

    /**
     * Gets id of the event
     * @return Integer representing id of the event
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets id of the event to eventId
     * @param eventId
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets date of the event
     * @return Date of the event
     */
    public LocalDate getEventDate() {
        return eventDate;
    }

    /**
     * Gets start time of the event
     * @return Start time of the event
     */
    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    /**
     * Gets end timme of the event
     * @return End time of the event
     */
    public LocalTime getEventEndTime() {
        return eventEndTime;
    }

    /**
     * Gets tags of the event
     * @return Set of tags
     */
    public Set<Tag> getEventTags() {
        return eventTags;
    }

    /**
     * Gets people associated with the event
     * @return Set representing people associated with the event
     */
    public Set<Person> getEventPeople() {
        return eventPeople;
    }

    /**
     * Gets description of the event
     * @return String representing description of the event
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Gets name of the event
     * @return String representing name of the event
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets nane of the event to eventName
     * @param eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets place of the event
     * @return String representing place of the event
     */
    public String getEventPlace() {
        return eventPlace;
    }

}
