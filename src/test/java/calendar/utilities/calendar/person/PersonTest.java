package calendar.utilities.calendar.person;

import com.gio.calendar.utilities.calendar.person.Person;

import static org.junit.Assert.assertEquals;

public class PersonTest {

    @org.junit.Test
    public void getPersonDetails() {
        Person t = new Person("abcd");
        assertEquals(t.getPersonDetails(), "abcd");
    }

    @org.junit.Test
    public void personToString() {
        Person t = new Person("abcd");
        assertEquals(t.toString(), "abcd");
    }
}