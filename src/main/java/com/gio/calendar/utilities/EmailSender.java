package com.gio.calendar.utilities;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.Person;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.AuthenticationException;
import java.util.Properties;
import java.util.function.Function;

/**
 * Class managing sending reminder emails
 */
public class EmailSender {
    private static String from = "autocalendar@mailtrap.io";
    private static Session session;
    private static Properties properties;
    private static final String username = "f2cc1c9d6586ce";
    private static final String password = "c83f5babdf43e8";
    private static boolean is_initialized = false;
    private static final String SMTP_HOST = "smtp.mailtrap.io";
    private static CheckedFunction<Session, Transport> transportProvider;

    /**
     * Setter for transport provider - useful for mocking
     * @param tp - Transport to use
     */
    public static void setTransportProvider(CheckedFunction<Session, Transport> tp) {
        transportProvider = tp;
    }

    /**
     * Initialize email sender with the relevant details (SMTP host, port, etc).
     */
    public static void initialize() {
        if (is_initialized)
            return;
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST); //SMTP Host
        props.put("mail.smtp.port", "2525"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        session = Session.getInstance(props, auth);
        transportProvider = (session) -> {
            Transport transport = null;
            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, username, password);
            return transport;
        };
        is_initialized = true;
    }

    /**
     * Sends an email to a recepient using SMTP.
     * @param to - email recepient
     * @param subject - subject of the email
     * @param messageText - text of the email
     * @return true if sending went successfully, otherwise false
     */
    private static boolean sendEmail(String to, String subject, String messageText) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(messageText);

            Transport transport = transportProvider.apply(session);
            Address[] addresses = new Address[1];
            addresses[0] = new InternetAddress(to);
            transport.sendMessage(message, addresses);
            transport.close();
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }

    /**
     * Send a reminder email about an event being created or modified
     * @param c - calendar event details
     * @param is_modified - boolean flag set if the event was already scheduled and is being modified
     * @return true if sending went successfully, otherwise false
     */
    public static boolean sendReminderEmail(CalendarEvent c, boolean is_modified) {
        if (!is_initialized)
            initialize();

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
