package com.gio.calendar.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "Person")
public class Person {

    @Id
    private String personEmail;

    @ManyToMany(mappedBy = "eventPeople")
    private Set<CalendarEvent> personEvents;

    public Person() {
    } // for persistance

    public Person(String personEmail) {
        this.personEmail = personEmail;
    }

    public static String peopleToString(Set<Person> people) {
        if (people.isEmpty()) {
            return "None";
        }
        List<String> ss = people.stream().map(Person::getPersonEmail).collect(Collectors.toList());
        return String.join(",", ss);
    }

    public static Set<Person> peopleFromString(String people) {
        if (people.equals("") || people.equals("None"))
            return new HashSet<Person>();
        return Arrays.stream(people.split(",")).map(Person::new).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(personEmail, person.personEmail);
    }

    @Override
    public String toString() {
        return personEmail;
    }

    @Override
    public int hashCode() {
        return Objects.hash(personEmail);
    }

    public String getPersonEmail() {
        return personEmail;
    }

}
