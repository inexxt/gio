package calendar.utilities.calendar.calendarevent;

import com.gio.calendar.utilities.calendar.calendarevent.CalendarEvent;
import com.gio.calendar.utilities.calendar.tag.Tag;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class CalendarEventTest {
	
	@org.junit.Test
	public void testName() {
		CalendarEvent c = new CalendarEvent(0,
											"NAME",  "DESC",
											LocalDate.now(),
											LocalTime.now(), LocalTime.now(),
											"first, second");

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


		c = new CalendarEvent(0,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  firstTimeCase, fourthTimeCase,
							  "first, second");

		assertEquals("13:45", c.getEventStartTimeString());

		c = new CalendarEvent(1,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  secondTimeCase, thirdTimeCase,
							  "first, second");

		assertEquals("00:05", c.getEventStartTimeString());

		c = new CalendarEvent(2,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  thirdTimeCase, fourthTimeCase,
							  "first, second");

		assertEquals("09:13", c.getEventStartTimeString());

		c = new CalendarEvent(3,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  fourthTimeCase, secondTimeCase,
							  "first, second");

		assertEquals("04:08", c.getEventStartTimeString());

		c = new CalendarEvent(4,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  fifthTimeCase, firstTimeCase,
							  "first, second");

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


		c = new CalendarEvent(0,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  secondTimeCase, firstTimeCase,
							  "first, second");

		assertEquals("13:45", c.getEventEndTimeString());

		c = new CalendarEvent(1,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  fifthTimeCase, secondTimeCase,
							  "first, second");

		assertEquals("00:05", c.getEventEndTimeString());

		c = new CalendarEvent(2,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  fourthTimeCase, thirdTimeCase,
							  "first, second");

		assertEquals("09:13", c.getEventEndTimeString());

		c = new CalendarEvent(3,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  firstTimeCase, fourthTimeCase,
							  "first, second");

		assertEquals("04:08", c.getEventEndTimeString());

		c = new CalendarEvent(4,
							  "NAME",  "DESC",
							  LocalDate.now(),
							  thirdTimeCase, fifthTimeCase,
							  "first, second");

		assertEquals("00:00", c.getEventEndTimeString());
	}


	@org.junit.Test
	public void testDescription() {
		CalendarEvent c = new CalendarEvent(0,
											"NAME",  "DESC",
											LocalDate.now(),
											LocalTime.now(), LocalTime.now(),
											"first, second");

		assertEquals("DESC", c.getEventDescription());
	}

	@org.junit.Test
	public void testTagsSplit() {
		CalendarEvent c = new CalendarEvent(1,
											"NAME", "DESC",
											LocalDate.now(),
											LocalTime.now(), LocalTime.now(),
											"first,second,third,fourth");
		
		Stream<String> stringStream = Stream.of("first", "second", "third", "fourth");
		
		assertEquals("first,second,third,fourth", c.getEventTags().toString());
	}
	@org.junit.Test
	public void testId() {
		CalendarEvent c = new CalendarEvent(132,
				"NAME",  "DESC",
				LocalDate.now(),
				LocalTime.now(), LocalTime.now(),
				"first, second");
		
		assertEquals(132, c.getEventId());
	}
}
