package com.gio.calendar.utilities;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.Person;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    private static String from = "autocalendar@localhost";
    private static Session session;
    private static Properties properties;

    public static void initialize() {
        Properties properties = System.getProperties();
        // Assuming you are sending email from localhost
        String host = "localhost";
        properties.setProperty("mail.smtp.host", host);
        session = Session.getDefaultInstance(properties);
    }

    static boolean sendEmail(String to, String subject, String messageText) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(messageText);
            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }

    public static boolean sendReminderEmail(CalendarEvent c, boolean is_modified) {
        String subject = "AutoCalendar: Reminder about an event " + c.getEventName();
        String messageText = "";
        messageText += "An event " + c.getEventName() + " is happening on " + c.getEventDate() + "\n";
        messageText += is_modified ? "(It has been rescheduled)\n" : "";
        messageText += "Start time: " + c.getEventStartTimeString() + " End time: " + c.getEventEndTimeString() + "\n";
        messageText += "Place: " + c.getEventPlace() + "\n";
        messageText += "Guests emails: " + c.getEventPeople() + "\n";
        messageText += "Description: " + c.getEventDescription() + "\n";
        boolean ok = true;
        for (Person p: c.getEventPeople()) {
            ok = ok && sendEmail(p.getPersonEmail(), subject, messageText);
        }
        return ok;
    }
}
