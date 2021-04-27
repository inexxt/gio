package com.gio.calendar.models;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="Tag")
public class Tag {
    @Id
    private String tagId;

    @ManyToMany(mappedBy = "eventTags")
    private Set<CalendarEvent> tagEvents;

    public String getTagId() {
        return tagId;
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
        if (tags.equals(""))
            return new HashSet<Tag>();
        return Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(tagId, tag.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }
}
