package com.gio.calendar.models;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "Tag")
/**
 * Represents tag
 */
public class Tag {
    @Id
    private String tagId;

    @ManyToMany(mappedBy = "eventTags")
    private Set<CalendarEvent> tagEvents;

    /**
     * Constructor for persistance.
     */
    public Tag() {
    }

    /**
     * Standard constructor for this class
     * @param tagId
     */
    public Tag(String tagId) {
        this.tagId = tagId;
    }

    /**
     * Maps tag to string
     * @return tagId as string
     */
    @Override
    public String toString() {
        return tagId;
    }

    /**
     * Returns string representing set of tags
     * @param tags
     * @return None if set is empty and list of tags joined with comma otherwise.
     */
    public static String tagsToString(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return "None";
        }
        List<String> ss = tags.stream().map(Tag::getTagId).collect(Collectors.toList());
        return String.join(",", ss);
    }

    /**
     * Returns set of tags given by string tags.
     * @param tags
     * @return Set of tags
     */
    public static Set<Tag> tagsFromString(String tags) {
        if (tags.equals("") || tags.equals("None"))
            return new HashSet<Tag>();
        return Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toSet());
    }

    /**
     * Returns true if o equals this object and false otherwise.
     * @param o
     * @return true if o equals this object and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(tagId, tag.tagId);
    }

    /**
     * Generates hash code of the object.
     * @return Integer representing hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }

    /**
     * Gets id of the tag.
     * @return Id of the tag.
     */
    public String getTagId() {
        return tagId;
    }
}
