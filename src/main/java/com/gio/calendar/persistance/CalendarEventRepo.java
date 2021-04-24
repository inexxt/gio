package com.gio.calendar.persistance;

import com.gio.calendar.database.ConnectionManager;
import com.gio.calendar.models.CalendarEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.EntityManager;
import java.util.List;
public class CalendarEventRepo {
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

    public static List<CalendarEvent> findByDate(LocalDate date) {
        return getEntityManager()
                .createNamedQuery("CalendarEvent.findByDate", CalendarEvent.class)
                .setParameter("date", date)
                .getResultList();
    }

    public static Optional<CalendarEvent> save(CalendarEvent event) {
        getEntityManager().getTransaction().begin();
        getEntityManager().persist(event);
        getEntityManager().getTransaction().commit();
        return Optional.of(event);
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
}
