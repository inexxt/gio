package com.gio.calendar.models;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="Tag")
public class Tag {
    @Id
    private String tagId;

    public Set<CalendarEvent> getTagEvents() {
        return tagEvents;
    }

    public void setTagEvents(Set<CalendarEvent> tagEvents) {
        this.tagEvents = tagEvents;
    }

    @ManyToMany(mappedBy = "eventTags")
    private Set<CalendarEvent> tagEvents;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Tag() { } // for persistance

    public Tag(String tagId) {
        this.tagId = tagId;
    }
    
    @Override
    public String toString() {
    	return tagId;
    }

    public static String tagsToString(Set<Tag> tags) {
        if (tags.isEmpty()) {
            return "None";
        }
        List<String> ss = tags.stream().map(Tag::getTagId).collect(Collectors.toList());
        return String.join(",", ss);
    }

    public static Set<Tag> tagsFromString(String tags) {
        return Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toSet());
    }
}
