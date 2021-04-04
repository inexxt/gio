package com.gio.calendar.utilities.calendar.tag;

import static org.junit.Assert.*;

public class TagTest {

    @org.junit.Test
    public void getTagName() {
        Tag t = new Tag("abcd");
        assertEquals(t.getTagName(), "abcd");
    }
}