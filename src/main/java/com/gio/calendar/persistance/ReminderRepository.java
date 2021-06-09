package com.gio.calendar.persistance;

import com.gio.calendar.database.ConnectionManager;
import com.gio.calendar.models.Reminder;
import org.springframework.stereotype.Repository;


import java.sql.SQLException;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Repository class managing the database operations for Reminders
 */
@Repository
public class ReminderRepository {

    /**
     * Tries to get the Reminder with the given id
     * @param id of Reminder
     * @return Optional of Reminder if it exists, otherwise empty Optional
     */
    public static Optional<Reminder> findById(Integer id) {
        Reminder reminder = getEntityManager().find(Reminder.class, id);
        return reminder != null ? Optional.of(reminder) : Optional.empty();
    }

    /**
     * Finds all Reminders for the given date available in the database
     * @param date
     * @return all Reminders for the given date
     */
    public static List<Reminder> findByDate(LocalDate date) {
        return getEntityManager()
                .createNamedQuery("Reminder.findByDate", Reminder.class)
                .setParameter("date", date)
                .getResultList();
    }

    /**
     * Saves Reminder to the databse, using transaction
     * @param reminder to save
     */
    public static void save(Reminder reminder) {
        getEntityManager().getTransaction().begin();
        getEntityManager().merge(reminder);
        getEntityManager().getTransaction().commit();
    }

    /**
     * Deletes Reminder from the databse, using transaction
     * @param reminderId of the reminder to delete
     */
    public static void deleteById(int reminderId) {
        Reminder reminder = getEntityManager().find(Reminder.class, reminderId);
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(reminder);
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
     * Updates an reminder details in the database, using transaction
     * @param reminderIdString - id of the reminder to update
     * @param reminderFromForm - new reminder details
     * @throws SQLException - if the operation is not successful
     */
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
