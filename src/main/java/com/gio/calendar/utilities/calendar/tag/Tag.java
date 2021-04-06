package com.gio.calendar.utilities.calendar.tag;

public class Tag {
    private String tagName;

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }
    
    @Override
    public String toString() {
    	return tagName;
    }
}
