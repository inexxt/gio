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
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reminderId;

    private LocalDate forDate;
    private LocalTime approxRemindTime;
    private String reminderContent;

    public Reminder()
    {}

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

    public Reminder(LocalDate forDate, LocalTime onTime, String reminderContent) {
        this.forDate = forDate;
        this.approxRemindTime = onTime;
        this.reminderContent = reminderContent;
    }

    public void update(Reminder otherReminder) {
        this.forDate = otherReminder.forDate;
        this.reminderContent = otherReminder.reminderContent;
    }

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public LocalDate getReminderDate() {
        return forDate;
    }

    public void setReminderDate(LocalDate forDate) {
        this.forDate = forDate;
    }

    public LocalTime getReminderTime() { return approxRemindTime; }

    public void setReminderTime(LocalTime remindTime) { this.approxRemindTime = remindTime; }

    public String getReminderContent() {
        return reminderContent;
    }

    public void setReminderContent(String reminderContent) {
        this.reminderContent = reminderContent;
    }
}
