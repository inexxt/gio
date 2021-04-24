package com.gio.calendar.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="Person")
public class Person {
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Id
    private String personName;

    public Set<CalendarEvent> getPersonEvents() {
        return personEvents;
    }

    public void setPersonEvents(Set<CalendarEvent> personEvents) {
        this.personEvents = personEvents;
    }

    @ManyToMany(mappedBy = "eventPeople")
    private Set<CalendarEvent> personEvents;

    public Person() { } // for persistance
    public Person(String personName) {
        this.personName = personName;
    }


    @Override
    public String toString() {
        return personName;
    }

    public static String peopleToString(Set<Person> people) {
        if (people.isEmpty()) {
            return "None";
        }
        List<String> ss = people.stream().map(Person::getPersonName).collect(Collectors.toList());
        return String.join(",", ss);
    }

    public static Set<Person> peopleFromString(String people) {
        return Arrays.stream(people.split(",")).map(Person::new).collect(Collectors.toSet());
    }
}
