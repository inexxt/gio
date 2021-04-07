package com.gio.calendar.utilities.calendar.person;

public class Person {
    private final String personName;

    public Person(String personName) {
        this.personName = personName;
    }

    public String getPersonDetails() {
        return personName;
    }

    @Override
    public String toString() {
        return personName;
    }
}
