package com.gio.calendar.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CalendarNote")
@NamedQueries({
        @NamedQuery(name = "CalendarNote.findById",
                query = "SELECT e FROM CalendarNote e WHERE e.noteId = :noteId"),
        @NamedQuery(name = "CalendarNote.findAll",
                query = "SELECT e FROM CalendarNote e"),
        @NamedQuery(name = "CalendarNote.findByDate",
                query = "SELECT e FROM CalendarNote e WHERE e.noteDate = :date")
})
/**
 * Represents calendar note.
 */
public class CalendarNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int noteId;
    private LocalDate noteDate;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "Note_Tag",
            joinColumns = @JoinColumn(name = "noteId"),
            inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    private Set<Tag> noteTags;

    private String noteDescription;
    private String noteName;

    /**
     * Constructor for persistance.
     */
    public CalendarNote() {
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
        CalendarNote that = (CalendarNote) o;
        return noteId == that.noteId &&
                Objects.equals(noteDate, that.noteDate) &&
                Objects.equals(noteTags, that.noteTags) &&
                Objects.equals(noteDescription, that.noteDescription) &&
                Objects.equals(noteName, that.noteName);
    }

    /**
     * Generates hash code of object.
     * @return Integer representing hash code of object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(noteId, noteDate, noteDescription, noteName);
    }

    /**
     * Standard constructor of this class
     * @param noteName
     * @param noteDescription
     * @param noteDate
     * @param tags
     */
    public CalendarNote(String noteName,
                         String noteDescription,
                         LocalDate noteDate,
                         String tags) {
        this.noteDate = noteDate;
        this.noteTags = Tag.tagsFromString(tags);
        this.noteDescription = noteDescription;
        this.noteName = noteName;
    }

    /**
     * Updates fields of the object with values from newNote
     * @param newNote
     */
    public void update(CalendarNote newNote) {
        this.noteDate = newNote.getNoteDate();
        this.noteDescription = newNote.getNoteDescription();
        this.noteName = newNote.getNoteName();
        this.noteTags = newNote.getNoteTags();
    }


    /**
     * Gets id of the note
     * @return Id of the note
     */
    public int getNoteId() {
        return noteId;
    }

    /**
     * Sets id of the note to noteId
     * @param noteId
     */
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    /**
     * Gets date of the note
     * @return Date of the note
     */
    public LocalDate getNoteDate() {
        return noteDate;
    }

    /**
     * Gets tags of the note
     * @return Set representing tags of the note
     */
    public Set<Tag> getNoteTags() {
        return noteTags;
    }

    /**
     * Gets description of the note
     * @return String representing description of the note
     */
    public String getNoteDescription() {
        return noteDescription;
    }

    /**
     * Gets name of the note
     * @return String representing name of the note
     */
    public String getNoteName() {
        return noteName;
    }

    /**
     * Sets name of the note to noteName
     * @param noteName
     */
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

}
