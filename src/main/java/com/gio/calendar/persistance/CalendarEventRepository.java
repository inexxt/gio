package com.gio.calendar.persistance;

import com.gio.calendar.database.ConnectionManager;
import com.gio.calendar.models.CalendarEvent;
import jdk.jfr.Event;
import org.springframework.stereotype.Repository;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.swing.text.html.Option;
import java.util.List;

/**
 * Repository class managing the database operations for CalendarEvents
 */
@Repository
public class CalendarEventRepository {
    /**
     * Tries to get the CalendarEvent with the given id
     * @param id of CalendarEvent
     * @return Optional of CalendarEvent if it exists, otherwise empty Optional
     */
    public static Optional<CalendarEvent> findById(Integer id) {
        CalendarEvent event = getEntityManager().find(CalendarEvent.class, id);
        return event != null ? Optional.of(event) : Optional.empty();
    }

    /**
     * Finds all CalendarEvents happening in the given range
     * @param startTime - start of the range, not inclusive
     * @param endTime - end of the range, not inclusive
     * @return list of CalendarEvents satisfying the condition
     */
    public static List<CalendarEvent> findByTime(LocalDateTime startTime, LocalDateTime endTime) {
        return getEntityManager()
                .createNamedQuery("CalendarEvent.findByTime", CalendarEvent.class)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getResultList();
    }

    /**
     * Finds all CalendarEvents available in the database
     * @return list of all CalendarEvents
     */
    public static List<CalendarEvent> findAll() {
        return getEntityManager()
                .createNamedQuery("CalendarEvent.findAll", CalendarEvent.class)
                .getResultList();
    }

    /**
     * Finds all CalendarEvents happening on the given date available in the database
     * @param date
     * @return all CalendarEvents happening on the given date
     */
    public static List<CalendarEvent> findByDate(LocalDate date) {
        return getEntityManager()
                .createNamedQuery("CalendarEvent.findByDate", CalendarEvent.class)
                .setParameter("date", date)
                .getResultList();
    }

    /**
     * Saves calendar event to the databse, using transaction
     * @param event to save
     */
    public static void save(CalendarEvent event) {
        getEntityManager().getTransaction().begin();
        getEntityManager().merge(event);
        getEntityManager().getTransaction().commit();
    }

    /**
     * Deletes calendar event from the databse, using transaction
     * @param eventId of the event to delete
     */
    public static void deleteById(int eventId) {
        CalendarEvent event = getEntityManager().find(CalendarEvent.class, eventId);
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(event);
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
     * Updates an event details in the database, using transaction
     * @param eventIdString - id of the event to update
     * @param eventFromForm - new event details
     * @throws SQLException - if the operation is not successful
     */
    public static void update(String eventIdString, CalendarEvent eventFromForm) throws SQLException {
        CalendarEvent event = getEntityManager().find(CalendarEvent.class, Integer.parseInt(eventIdString));
        if (event == null) {
            throw new SQLException("Event with id " + eventIdString + " not found in database");
        }
        getEntityManager().detach(event);
        event.update(eventFromForm);
        getEntityManager().merge(event);
    }
}
