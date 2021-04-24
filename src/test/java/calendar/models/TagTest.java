package calendar.models;

import com.gio.calendar.models.Tag;

import static org.junit.Assert.assertEquals;

public class TagTest {

    @org.junit.Test
    public void getTagName() {
        Tag t = new Tag("abcd");
        assertEquals(t.getTagId(), "abcd");
    }

    @org.junit.Test
    public void testTagsSplitJoin() {
        String tags = "first,second,third,fourth";
        assertEquals(Tag.tagsFromString(tags),
                Tag.tagsFromString(Tag.tagsToString(Tag.tagsFromString(tags))));

    }
}