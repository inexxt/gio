package calendar.models;

import com.gio.calendar.models.Person;
import com.gio.calendar.models.Tag;

import java.util.HashSet;

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
    public void peopleTagsSplitJoinNonEmpty() {
        String people = "first,second,third,fourth";
        assertEquals(Person.peopleFromString(people),
                Person.peopleFromString(Person.peopleToString(Person.peopleFromString(people))));
    }

    @org.junit.Test
    public void peoplePersonsSplitJoinEmpty() {
        String people = "";
        assertEquals(new HashSet<Person>(),
                Person.peopleFromString(Person.peopleToString(Person.peopleFromString(people))));
    }
}