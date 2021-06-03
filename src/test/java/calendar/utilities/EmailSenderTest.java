package calendar.utilities;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.utilities.EmailSender;
import org.junit.Before;
import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EmailSenderTest {

    @Test
    public void sendReminderEmail() throws MessagingException {
        CalendarEvent c = new CalendarEvent("NAME", "DESC",
                LocalDate.now(),
                LocalTime.now(), LocalTime.now(),
                "first, second",
                "Warsaw", "aaa@revutap.com");
        EmailSender.initialize();
        EmailSender.setTransportProvider((session) -> new TransportMock(session, new URLName("test")));
        EmailSender.sendReminderEmail(c, false);

        assertEquals(TransportMock.sentAddresses.size(), 1);
        assertEquals(TransportMock.sentAddresses.get(0).length, 1);
        assertEquals(TransportMock.sentAddresses.get(0)[0], new InternetAddress("aaa@revutap.com"));

        assertEquals(TransportMock.sentMessages.size(), 1);
        assertEquals(TransportMock.sentMessages.get(0).getSubject(), "AutoCalendar: Reminder about an event " + c.getEventName());
    }

    private static class TransportMock extends Transport {
        public static List<Message> sentMessages = new ArrayList<>();
        public static List<Address[]> sentAddresses = new ArrayList<>();

        public TransportMock(Session session, URLName urlname) {
            super(session, urlname);
        }

        @Override
        public void sendMessage(Message message, Address[] addresses) throws MessagingException {
            sentMessages.add(message);
            sentAddresses.add(addresses);
        }
    }
}