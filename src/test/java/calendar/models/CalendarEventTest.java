package calendar.models;

import com.gio.calendar.models.CalendarEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CalendarEventTest {
	
	@org.junit.Test
	public void testName() {
		CalendarEvent c = new CalendarEvent("NAME",  "DESC",
											LocalDate.now(),
											LocalTime.now(), LocalTime.now(),
											"first, second",
										"Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("NAME", c.getEventName());
	}

	@org.junit.Test
	public void testStartTimeString() {
		CalendarEvent c;

		LocalTime firstTimeCase = LocalTime.of(13,  45);
		LocalTime secondTimeCase = LocalTime.of(0, 5);
		LocalTime thirdTimeCase = LocalTime.of(9, 13);
		LocalTime fourthTimeCase = LocalTime.of(4, 8);
		LocalTime fifthTimeCase = LocalTime.of(0, 0);


		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  firstTimeCase, fourthTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("13:45", c.getEventStartTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  secondTimeCase, thirdTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("00:05", c.getEventStartTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  thirdTimeCase, fourthTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("09:13", c.getEventStartTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  fourthTimeCase, secondTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("04:08", c.getEventStartTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  fifthTimeCase, firstTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("00:00", c.getEventStartTimeString());
	}

	@org.junit.Test
	public void testEndTimeString() {
		CalendarEvent c;

		LocalTime firstTimeCase = LocalTime.of(13,  45);
		LocalTime secondTimeCase = LocalTime.of(0, 5);
		LocalTime thirdTimeCase = LocalTime.of(9, 13);
		LocalTime fourthTimeCase = LocalTime.of(4, 8);
		LocalTime fifthTimeCase = LocalTime.of(0, 0);


		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  secondTimeCase, firstTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("13:45", c.getEventEndTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  fifthTimeCase, secondTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("00:05", c.getEventEndTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  fourthTimeCase, thirdTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("09:13", c.getEventEndTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  firstTimeCase, fourthTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("04:08", c.getEventEndTimeString());

		c = new CalendarEvent("NAME",  "DESC",
							  LocalDate.now(),
							  thirdTimeCase, fifthTimeCase,
							  "first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("00:00", c.getEventEndTimeString());
	}


	@org.junit.Test
	public void testDescription() {
		CalendarEvent c = new CalendarEvent("NAME",  "DESC",
											LocalDate.now(),
											LocalTime.now(), LocalTime.now(),
											"first, second","Warsaw","p1@wp.pl,p2@wp.pl");

		assertEquals("DESC", c.getEventDescription());
	}
}