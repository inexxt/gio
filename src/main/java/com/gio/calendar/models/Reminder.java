package com.gio.calendar.models;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;
import java.time.LocalDate;

@Entity
@Table(name = "Reminder")
@NamedQueries({
        @NamedQuery(name = "Reminder.findByDate",
                query = "SELECT e FROM Reminder e WHERE e.forDate = :date")
})
/**
 * Represents reminder
 */
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reminderId;

    private LocalDate forDate;
    private LocalTime approxRemindTime;
    private String reminderContent;

    /**
     * Constructor for persistance.
     */
    public Reminder()
    {}

    /**
     * Returns true if o equals this object and false otherwise.
     * @param o
     * @return true if o equals this object and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Reminder other = (Reminder) o;

        return reminderId == other.reminderId &&
               Objects.equals(forDate, ((Reminder) o).forDate);
    }

    /**
     * Standard constructor for this class
     * @param forDate
     * @param onTime
     * @param reminderContent
     */
    public Reminder(LocalDate forDate, LocalTime onTime, String reminderContent) {
        this.forDate = forDate;
        this.approxRemindTime = onTime;
        this.reminderContent = reminderContent;
    }

    /**
     * Updates fields of the remainder with values from otherReminder
     * @param otherReminder
     */
    public void update(Reminder otherReminder) {
        this.forDate = otherReminder.forDate;
        this.reminderContent = otherReminder.reminderContent;
    }

    /**
     * Gets id of the remainder
     * @return Id of the remainder
     */
    public int getReminderId() {
        return reminderId;
    }

    /**
     * Sets id of the remainder to reminderId
     * @param reminderId
     */
    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    /**
     * Gets date of the remainder
     * @return Date of the remainder
     */
    public LocalDate getReminderDate() {
        return forDate;
    }

    /**
     * Sets date of the remainder to forDate
     * @param forDate
     */
    public void setReminderDate(LocalDate forDate) {
        this.forDate = forDate;
    }

    /**
     * Gets time of the remainder
     * @return Time of the remainder
     */
    public LocalTime getReminderTime() { return approxRemindTime; }

    /**
     * Sets time of the remainder to remindTime
     * @param remindTime
     */
    public void setReminderTime(LocalTime remindTime) { this.approxRemindTime = remindTime; }

    /**
     * Gets content of the remainder
     * @return String representing content of the remainder
     */
    public String getReminderContent() {
        return reminderContent;
    }

    /**
     * Sets content of the reminder to reminderContent
     * @param reminderContent
     */
    public void setReminderContent(String reminderContent) {
        this.reminderContent = reminderContent;
    }
}
