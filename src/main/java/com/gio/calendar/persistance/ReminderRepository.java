package com.gio.calendar.persistance;

import com.gio.calendar.database.ConnectionManager;
import com.gio.calendar.models.Reminder;
import org.springframework.stereotype.Repository;


import java.sql.SQLException;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class ReminderRepository {
    public static Optional<Reminder> findById(Integer id) {
        Reminder reminder = getEntityManager().find(Reminder.class, id);
        return reminder != null ? Optional.of(reminder) : Optional.empty();
    }

    public static List<Reminder> findByDate(LocalDate date) {
        return getEntityManager()
                .createNamedQuery("Reminder.findByDate", Reminder.class)
                .setParameter("date", date)
                .getResultList();
    }

    public static void save(Reminder reminder) {
        getEntityManager().getTransaction().begin();
        getEntityManager().merge(reminder);
        getEntityManager().getTransaction().commit();
    }

    public static void deleteById(int reminderId) {
        Reminder reminder = getEntityManager().find(Reminder.class, reminderId);
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(reminder);
        getEntityManager().getTransaction().commit();
    }

    private static EntityManager getEntityManager() {
        return ConnectionManager.getEntityManager();
    }

    public static void update(String reminderIdString, Reminder reminderFromForm) throws SQLException {
        Reminder reminder = getEntityManager().find(Reminder.class, Integer.parseInt(reminderIdString));
        if(reminder == null) {
            throw new SQLException("Reminder with id " + reminderIdString + " not found in database");
        }
        getEntityManager().detach(reminder);
        reminder.update(reminderFromForm);
        getEntityManager().merge(reminder);
    }
}
