package com.gio.calendar.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "Person")
/**
 * Represents person
 */
public class Person {

    @Id
    private String personEmail;

    @ManyToMany(mappedBy = "eventPeople")
    private Set<CalendarEvent> personEvents;

    /**
     * Constructor for persistance.
     */
    public Person() {
    }

    /**
     * Standard constructor of this class
     * @param personEmail
     */
    public Person(String personEmail) {
        this.personEmail = personEmail;
    }

    /**
     * Returns String representing people from set people
     * @param people
     * @return None if set is empty, otherwise list of emails representing people joined by comma.
     */
    public static String peopleToString(Set<Person> people) {
        if (people.isEmpty()) {
            return "None";
        }
        List<String> ss = people.stream().map(Person::getPersonEmail).collect(Collectors.toList());
        return String.join(",", ss);
    }

    /**
     * Returns set representing people given by string people (people are separated by comma)
     * @param people
     * @return Set representing people
     */
    public static Set<Person> peopleFromString(String people) {
        if (people.equals("") || people.equals("None"))
            return new HashSet<Person>();
        return Arrays.stream(people.split(",")).map(Person::new).collect(Collectors.toSet());
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
        Person person = (Person) o;
        return Objects.equals(personEmail, person.personEmail);
    }

    /**
     * Returns string (email) representing person.
     * @return String (email) representing person
     */
    @Override
    public String toString() {
        return personEmail;
    }

    /**
     * Generates hash code of the object.
     * @return Integer representing hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(personEmail);
    }

    /**
     * Returns email of the person.
     * @return String representing email of the person
     */
    public String getPersonEmail() {
        return personEmail;
    }

}
