package com.gio.calendar.persistance;

import com.gio.calendar.database.ConnectionManager;
import com.gio.calendar.models.CalendarNote;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Repository class managing the database operations for CalendarNotes
 */
@Repository
public class CalendarNoteRepository {
    /**
     * Tries to get the CalendarNote with the given id
     * @param id of CalendarEvent
     * @return Optional of CalendarNote if it exists, otherwise empty Optional
     */
    public static Optional<CalendarNote> findById(Integer id) {
        CalendarNote note = getEntityManager().find(CalendarNote.class, id);
        return note != null ? Optional.of(note) : Optional.empty();
    }

    /**
     * Finds all CalendarNotes available in the database
     * @return list of all CalendarNotes
     */
    public static List<CalendarNote> findAll() {
        return getEntityManager()
                .createNamedQuery("CalendarNote.findAll", CalendarNote.class)
                .getResultList();
    }

    /**
     * Finds all CalendarNotes for the given date available in the database
     * @param date
     * @return all CalendarNotes happening on the given date
     */
    public static List<CalendarNote> findByDate(LocalDate date) {
        return getEntityManager()
                .createNamedQuery("CalendarNote.findByDate", CalendarNote.class)
                .setParameter("date", date)
                .getResultList();
    }
    /**
     * Saves calendar note to the databse, using transaction
     * @param note to save
     */
    public static void save(CalendarNote note) {
        getEntityManager().getTransaction().begin();
        getEntityManager().merge(note);
        getEntityManager().getTransaction().commit();
    }

    /**
     * Deletes calendar note from the databse, using transaction
     * @param noteId of the event to delete
     */
    public static void deleteById(int noteId) {
        CalendarNote note = getEntityManager().find(CalendarNote.class, noteId);
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(note);
        getEntityManager().getTransaction().commit();
    }

    /**
     * Helper method to get entity manager
     * @return EntityManager
     */
    private static EntityManager getEntityManager() {
        return ConnectionManager.getEntityManager();
    }

    /**
     * Updates an event note details in the database, using transaction
     * @param noteIdString - id of the note to update
     * @param noteFromForm - new note details
     * @throws SQLException - if the operation is not successful
     */
    public static void update(String noteIdString, CalendarNote noteFromForm) throws SQLException {
        CalendarNote note = getEntityManager().find(CalendarNote.class, Integer.parseInt(noteIdString));
        if (note == null) {
            throw new SQLException("Note with id " + noteIdString + " not found in database");
        }
        getEntityManager().detach(note);
        note.update(noteFromForm);
        getEntityManager().merge(note);
    }
}
