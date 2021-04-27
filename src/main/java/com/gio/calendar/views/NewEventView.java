package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.Person;
import com.gio.calendar.models.Tag;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.dependency.CssImport;

import java.time.LocalDate;
import java.util.*;

@Route(value = "new_event", layout = MainView.class)
@PageTitle("New event")
@CssImport("./views/newevent/newevent-view.css")
public class NewEventView extends Div {
    /**
     * Maximum number of characters that event's name can contain
     */
    private static final Integer EVENT_NAME_CHARACTERS_LIMIT = 180;

    /**
     * Maximum number of characters that event's description can contain
     */
    private static final Integer EVENT_DESCRIPTION_CHARACTERS_LIMIT = 750;

    private static final Integer EVENT_TAGS_CHARACTERS_LIMIT = 180;

    private final Button addEventButton;

    private final DatePicker eventDatePicker;

    private final TimePicker eventStartTimePicker;
    private final TimePicker eventEndTimePicker;

    private final TextArea eventNameArea;
    private final TextArea eventDescriptionArea;
    private final TextArea tagsField;
    private final TextArea peopleField;
    private final TextArea eventPlaceField;
    private final TextArea eventRepNumField;
    private final TextArea eventRepBreakField;

    private final HorizontalLayout eventDateLayout;
    private final HorizontalLayout eventDescriptionLayout;
    private final HorizontalLayout eventNameLayout;

    private final HorizontalLayout eventStartTimeLayout;
    private final HorizontalLayout eventEndTimeLayout;
    private final HorizontalLayout eventRepetitionNumberLayout;
    private final HorizontalLayout eventRepetitionBreakLayout;
    private final HorizontalLayout tagsFieldLayout;
    private final HorizontalLayout peopleFieldLayout;
    private final HorizontalLayout eventPlaceFieldLayout;

    private void handleSqlException(Exception e) {
        Notification.show("SQLException occurred. Event has not been added.");
        Notification.show("SQLException occurred: " + e.getMessage());
        Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
    }

    private void handleError(String e) {
        Notification.show("Error occurred: " + e);
    }

    private void handleAfterInitialCheck(String eventIdString) {
        /* Non-null eventIdString indicates that we are interested in modifying
         * event data
         */
        if(eventIdString != null) {
            modifyEventHandler(eventIdString);
            Notification.show("Event successfully modified!");

            /* Only modifying the event, we can exit the function now
             */
            return;
        }

        Integer repetitionsNumber = 1;
        Integer daysBetweenEventRepetitions = 0;

        try {
            if(eventRepNumField.isEmpty()) {
                Notification.show("Error: number of event repetitions has not been provided");
                return;
            }

            repetitionsNumber = Integer.parseInt(eventRepNumField.getValue());

            /* If event is to be repeated more than once, information about days interval
             * between event repetitions is needed
             */
            if(repetitionsNumber > 1) {
                if(eventRepBreakField.isEmpty()) {
                    Notification.show("Error: number of days between event repetitions has not been provided");
                    return;
                }

                daysBetweenEventRepetitions = Integer.parseInt(eventRepBreakField.getValue());
            }
        }
        /* Incorrect characters in field(s)
         */
        catch(NumberFormatException ex) {
            Notification.show("Error: incorrect value provided at repetitions / days break number field");
            return;
        }

        /* Offset (in days) from the original date (provided in form).
         * User for inserting repetitions of the event
         */
        Long deltaDays = 0L;

        for(Integer i = 0; i < repetitionsNumber; i++) {
            addEventHandler(deltaDays);

            /* Update days offset from provided event date
             */
            deltaDays += daysBetweenEventRepetitions.longValue();
        }

        Notification.show("Event " + eventNameArea.getValue() + " was created!");

        clearForm();

    }

    private void addEventHandler(long daysDeltaFromOrigin) {
        Optional<String> err = Optional.empty();
        try {
            CalendarEvent event = getEventFromForm(daysDeltaFromOrigin);
            err = CalendarEventRepository.save(event);
        }
        catch(IllegalArgumentException e) {
            handleSqlException(e);
        }
        err.ifPresent(this::handleError);
    }

    private CalendarEvent getEventFromForm(long daysDeltaFromOrigin) {
        LocalDate eventDate = eventDatePicker.getValue();
        eventDate.plusDays(daysDeltaFromOrigin);

        return new CalendarEvent(
                eventNameArea.getValue(),
                eventDescriptionArea.getValue(),
                eventDate,
                eventStartTimePicker.getValue(),
                eventEndTimePicker.getValue(),
                tagsField.getValue(),
                eventPlaceField.getValue(),
                peopleField.getValue());
    }

    private void modifyEventHandler(String eventIdString) {
        Optional<String> err = Optional.empty();
        try {
            err = CalendarEventRepository.update(eventIdString, getEventFromForm(0));
        } catch (Exception e) {
            handleSqlException(e);
        }
        finally {
            err.ifPresent(this::handleError);
        }
    }

    private void clearForm() {
        eventNameArea.clear();
        eventDescriptionArea.clear();
        eventDatePicker.clear();
        eventEndTimePicker.clear();
        eventStartTimePicker.clear();
        eventPlaceField.clear();
        tagsField.clear();
        peopleField.clear();
    }

    public NewEventView() {
        addClassName("newevent-view");
        
        String eventIdString = VaadinService.getCurrentRequest().getParameter("event_id");
        
        /* Picker of the new event start time */
        eventStartTimePicker = new TimePicker();
        eventStartTimePicker.setLabel("Choose or type in event start time (required):");
        eventStartTimePicker.setRequired(true);

        /* Picker of the new event end time */
        eventEndTimePicker = new TimePicker();
        eventEndTimePicker.setLabel("Choose or type in event end time (required):");
        eventEndTimePicker.setRequired(true);

        /* Picker of the new event date */
        eventDatePicker = new DatePicker();
        eventDatePicker.setLabel("Choose event date (required)");
        eventDatePicker.setRequired(true);

        /* Text area for new event name */
        eventNameArea = new TextArea("Event name (optional). Maximum length: " +
                                     EVENT_NAME_CHARACTERS_LIMIT.toString());

        eventNameArea.setMaxLength(EVENT_NAME_CHARACTERS_LIMIT);

        /* Text area for new event description */
        eventDescriptionArea = new TextArea("Event description (optional). Maximum length: " +
                                            EVENT_DESCRIPTION_CHARACTERS_LIMIT.toString());

        eventDescriptionArea.setMaxLength(EVENT_DESCRIPTION_CHARACTERS_LIMIT);

        tagsField = new TextArea("Event tags (optional). Should be separated by coma. Maximum length: " + EVENT_TAGS_CHARACTERS_LIMIT);
        tagsField.setMaxLength(EVENT_TAGS_CHARACTERS_LIMIT);

        peopleField = new TextArea("Guests (optional). A list of guests separated by coma. Maximum length: " + EVENT_TAGS_CHARACTERS_LIMIT);
        peopleField.setMaxLength(EVENT_TAGS_CHARACTERS_LIMIT);

        eventPlaceField = new TextArea("Event place (optional). Maximum length: " + EVENT_TAGS_CHARACTERS_LIMIT);
        eventPlaceField.setMaxLength(EVENT_TAGS_CHARACTERS_LIMIT);

        eventRepNumField = new TextArea("Number of event repetitions (required). Default: 1 " +
                "(do not change if you wish that the event occurs only once");
        eventRepNumField.setMaxLength(4);
        eventRepNumField.setValue(Integer.toString(1));

        eventRepBreakField = new TextArea("Time interval (in days) between repetitions of event, required for more than event " +
                "repetition");

        /* Layouts creating */
        eventDateLayout = new HorizontalLayout();
        eventDescriptionLayout = new HorizontalLayout();
        eventNameLayout = new HorizontalLayout();
        eventStartTimeLayout = new HorizontalLayout();
        eventEndTimeLayout = new HorizontalLayout();
        eventRepetitionNumberLayout = new HorizontalLayout();
        eventRepetitionBreakLayout = new HorizontalLayout();
        tagsFieldLayout = new HorizontalLayout();
        peopleFieldLayout = new HorizontalLayout();
        eventPlaceFieldLayout = new HorizontalLayout();

        /* Button for confirming new event add operation */
        addEventButton = (eventIdString == null ? new Button("Add event") : new Button("Modify event"));

        /* Enrich layouts with created components */
        eventDateLayout.addAndExpand(eventDatePicker);
        eventDescriptionLayout.addAndExpand(eventDescriptionArea);
        eventNameLayout.addAndExpand(eventNameArea);
        eventStartTimeLayout.addAndExpand(eventStartTimePicker);
        eventEndTimeLayout.addAndExpand(eventEndTimePicker);
        eventRepetitionNumberLayout.addAndExpand(eventRepNumField);
        eventRepetitionBreakLayout.addAndExpand(eventRepBreakField);
        tagsFieldLayout.addAndExpand(tagsField);
        peopleFieldLayout.addAndExpand(peopleField);
        eventPlaceFieldLayout.addAndExpand(eventPlaceField);

        /* Add all layouts */
        add(eventDateLayout, eventNameLayout, eventDescriptionLayout,
            eventStartTimeLayout, eventEndTimeLayout, eventRepetitionNumberLayout,
            eventRepetitionBreakLayout, tagsFieldLayout, peopleFieldLayout,
            eventPlaceFieldLayout, addEventButton);
        
        if(eventIdString != null) {
            Optional<CalendarEvent> event = Optional.empty();
            try {
                event = CalendarEventRepository.findById(Integer.parseInt(eventIdString));
        	} catch (IllegalArgumentException e) {
        	    handleSqlException(e);
            }
            if(event.isPresent()) {
                eventNameArea.setValue(event.get().getEventName());
                eventDescriptionArea.setValue(event.get().getEventDescription());
                eventStartTimePicker.setValue(event.get().getEventStartTime());
                eventEndTimePicker.setValue(event.get().getEventEndTime());
                eventDatePicker.setValue(event.get().getEventDate());
                eventPlaceField.setValue(event.get().getEventPlace());
                tagsField.setValue(Tag.tagsToString(event.get().getEventTags()));
                peopleField.setValue(Person.peopleToString(event.get().getEventPeople()));
                /* Disable options of event repetitions */
                eventRepNumField.setEnabled(false);
                eventRepBreakField.setEnabled(false);
            }
            else {
                Notification.show("Event with id " + eventIdString + " not found.");
            }
        }

        /* Listener for the Button object which is to add the event on click after
        *  checking correctness of event input data
        */
        addEventButton.addClickListener(e -> {
        	/* Check for the case when event start time has not been provided.
        	 */
        	if(eventStartTimePicker.getValue() == null) {
        		Notification.show("Error: event start time has not been provided.");
        	}
        	/* Check for the case when event end time has not been provided.
        	 */
        	else if(eventEndTimePicker.getValue() == null) {
        		Notification.show("Error: event end time has not been provided.");
        	}
            /*  Check for the case when both start and event end time were provided but according
             *  to the information given, event is to end before or the moment it starts.
             *  Prevent event adding by issuing an error message.
             */
        	else if(eventStartTimePicker.getValue().compareTo(eventEndTimePicker.getValue()) >= 0) {
                Notification.show("Error: event start time later or just the event end time.");
            }
            /*  Check if no event date has been provided and issue an error message in such case
             */
            else if(eventDatePicker.getValue() == null) {
                Notification.show("Error: event date has not been provided.");
            }
            else {
                /* Initial checks passed, further actions can be taken
                 */
                handleAfterInitialCheck(eventIdString);
            }
        });
    }
}
