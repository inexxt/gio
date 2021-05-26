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

    public CalendarNote() {
    } // for persistance

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

    @Override
    public int hashCode() {
        return Objects.hash(noteId, noteDate, noteDescription, noteName);
    }

    public CalendarNote(String noteName,
                         String noteDescription,
                         LocalDate noteDate,
                         String tags) {
        this.noteDate = noteDate;
        this.noteTags = Tag.tagsFromString(tags);
        this.noteDescription = noteDescription;
        this.noteName = noteName;
    }

    public void update(CalendarNote newNote) {
        this.noteDate = newNote.getNoteDate();
        this.noteDescription = newNote.getNoteDescription();
        this.noteName = newNote.getNoteName();
        this.noteTags = newNote.getNoteTags();
    }


    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public LocalDate getNoteDate() {
        return noteDate;
    }

    public Set<Tag> getNoteTags() {
        return noteTags;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

}
