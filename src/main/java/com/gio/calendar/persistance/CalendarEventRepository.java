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

@Repository
public class CalendarEventRepository {
    public static Optional<CalendarEvent> findById(Integer id) {
        CalendarEvent event = getEntityManager().find(CalendarEvent.class, id);
        return event != null ? Optional.of(event) : Optional.empty();
    }

    public static List<CalendarEvent> findByTime(LocalDateTime startTime, LocalDateTime endTime) {
        return getEntityManager()
                .createNamedQuery("CalendarEvent.findByTime", CalendarEvent.class)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getResultList();
    }

    public static List<CalendarEvent> findAll() {
        return getEntityManager()
                .createNamedQuery("CalendarEvent.findAll", CalendarEvent.class)
                .getResultList();
    }

    public static List<CalendarEvent> findByDate(LocalDate date) {
        return getEntityManager()
                .createNamedQuery("CalendarEvent.findByDate", CalendarEvent.class)
                .setParameter("date", date)
                .getResultList();
    }

    public static void save(CalendarEvent event) {
        getEntityManager().getTransaction().begin();
        getEntityManager().merge(event);
        getEntityManager().getTransaction().commit();
    }

    public static void deleteById(int eventId) {
        CalendarEvent event = getEntityManager().find(CalendarEvent.class, eventId);
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(event);
        getEntityManager().getTransaction().commit();
    }

    private static EntityManager getEntityManager() {
        return ConnectionManager.getEntityManager();
    }

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
