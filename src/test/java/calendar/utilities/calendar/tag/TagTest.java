package calendar.utilities.calendar.tag;

import com.gio.calendar.utilities.calendar.tag.Tag;

import static org.junit.Assert.assertEquals;

public class TagTest {

    @org.junit.Test
    public void getTagName() {
        Tag t = new Tag("abcd");
        assertEquals(t.getTagName(), "abcd");
    }
}