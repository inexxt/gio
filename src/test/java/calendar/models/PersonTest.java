package calendar.models;

import com.gio.calendar.models.Person;

import static org.junit.Assert.assertEquals;

public class PersonTest {

    @org.junit.Test
    public void getPersonDetails() {
        Person t = new Person("abcd");
        assertEquals(t.getPersonName(), "abcd");
    }

    @org.junit.Test
    public void personToString() {
        Person t = new Person("abcd");
        assertEquals(t.toString(), "abcd");
    }

    @org.junit.Test
    public void testPeopleSplitJoin() {
        String people = "first,second,third,fourth";
        assertEquals(Person.peopleFromString(people),
                Person.peopleFromString(Person.peopleToString(Person.peopleFromString(people))));
    }
}