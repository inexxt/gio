package com.gio.calendar.persistance;

import com.gio.calendar.database.ConnectionManager;
import com.gio.calendar.models.CalendarNote;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class CalendarNoteRepository {
    public static Optional<CalendarNote> findById(Integer id) {
        CalendarNote note = getEntityManager().find(CalendarNote.class, id);
        return note != null ? Optional.of(note) : Optional.empty();
    }

    public static List<CalendarNote> findAll() {
        return getEntityManager()
                .createNamedQuery("CalendarNote.findAll", CalendarNote.class)
                .getResultList();
    }

    public static List<CalendarNote> findByDate(LocalDate date) {
        return getEntityManager()
                .createNamedQuery("CalendarNote.findByDate", CalendarNote.class)
                .setParameter("date", date)
                .getResultList();
    }

    public static void save(CalendarNote note) {
        getEntityManager().getTransaction().begin();
        getEntityManager().merge(note);
        getEntityManager().getTransaction().commit();
    }

    public static void deleteById(int eventId) {
        CalendarNote note = getEntityManager().find(CalendarNote.class, eventId);
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(note);
        getEntityManager().getTransaction().commit();
    }

    private static EntityManager getEntityManager() {
        return ConnectionManager.getEntityManager();
    }

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
