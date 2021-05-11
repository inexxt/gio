package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.Person;
import com.gio.calendar.models.Tag;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.vaadin.flow.component.UI;
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
import java.time.LocalTime;
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

    /**
     * Maximum number of characters that event tags' field can contain
     */
    private static final Integer EVENT_TAGS_CHARACTERS_LIMIT = 180;

    private Button addEventButton;

    private DatePicker eventDatePicker;

    private TimePicker eventStartTimePicker;
    private TimePicker eventEndTimePicker;

    private TextArea eventNameArea;
    private TextArea eventDescriptionArea;
    private TextArea tagsField;
    private TextArea peopleField;
    private TextArea eventPlaceField;
    private TextArea eventRepNumField;
    private TextArea eventRepBreakField;

    private Div eventDateTimeDiv;
    private Div eventNameDescDiv;
    private Div eventRepDiv;
    private Div eventMiscDataDiv;

    private HorizontalLayout eventDateAndTimeDivLayout;
    private HorizontalLayout eventDateAndTimeLayout;
    private HorizontalLayout eventNameAndDescriptionDivLayout;
    private HorizontalLayout eventNameAndDescriptionLayout;
    private HorizontalLayout eventRepetitionsDivLayout;
    private HorizontalLayout eventRepetitionsLayout;
    private HorizontalLayout eventMiscDataDivLayout;
    private HorizontalLayout eventMiscDataLayout;

    private void handleSqlException(Exception e) {
        Notification.show("SQLException occurred. Event has not been added.");
        Notification.show("SQLException occurred: " + e.getMessage());
        Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
    }

    private void handleError(String e) {
        Notification.show("Error occurred: " + e);
    }

    private boolean checkTimeIntervalString() {
        String targetString = eventRepBreakField.getValue();

        if(targetString.length() == 1) {
            return false;
        }

        char unitCharacter = targetString.charAt(targetString.length() - 1);
        System.out.println(unitCharacter);

        if(unitCharacter != 'D' && unitCharacter != 'W' &&
           unitCharacter != 'M' && unitCharacter != 'Y') {
            return false;
        }

        String integerString = targetString.substring(0, targetString.length() - 1);
        Integer numberOfUnits;

        try {
            numberOfUnits = Integer.parseInt(integerString);
            System.out.println(numberOfUnits);
        }
        catch(NumberFormatException e) {
            /* Bad integer format */
            return false;
        }

        if(numberOfUnits.intValue() <= 0) {
            return false;
        }

        return true;
    }

    private void handleAfterInitialCheck(String eventIdString) {
        /* Non-null eventIdString indicates that we are interested in modifying
         * event data
         */
        if (eventIdString != null) {
            modifyEventHandler(eventIdString);
            Notification.show("Event successfully modified!");
            return; /* Only modifying the event, we can exit the function now */
        }

        Integer repetitionsNumber = 1;
        Integer unitsOfTimeBetweenRepetitions = 0;

        try {
            if (eventRepNumField.isEmpty()) {
                Notification.show("Error: number of event repetitions has not been provided");
                return;
            }

            repetitionsNumber = Integer.parseInt(eventRepNumField.getValue());

            /* If event is to be repeated more than once, information about days interval
             * between event repetitions is needed
             */
            if (repetitionsNumber > 1) {
                if (eventRepBreakField.isEmpty()) {
                    Notification.show("Error: number of days between event repetitions has not been provided");
                    return;
                }

                if(!checkTimeIntervalString()) {
                    Notification.show("Error: incorrect format for time interval between event repetitions");
                    return;
                }
            }
        }
        /* Incorrect characters in some fields */
        catch (NumberFormatException ex) {
            Notification.show("Error: incorrect value provided at repetitions / days break number field");
            return;
        }

        String eventRepIntervalString = eventRepBreakField.getValue();
        int repIntervalStrLength = eventRepIntervalString.length();

        unitsOfTimeBetweenRepetitions = Integer.parseInt(eventRepIntervalString.substring(0, repIntervalStrLength - 1));

        int timeUnitType;
        char unitCharacter = eventRepIntervalString.charAt(repIntervalStrLength - 1);

        if(unitCharacter == 'D') {
            timeUnitType = 0;
        }
        else if(unitCharacter == 'W') {
            timeUnitType = 1;
        }
        else if(unitCharacter == 'M') {
            timeUnitType= 2;
        }
        else {
            timeUnitType = 3;
        }

        long deltaUnit = 0L;

        for (int i = 0; i < repetitionsNumber; i++) {
            addEventHandler(deltaUnit, timeUnitType);
            /* Update time units offset from provided event date */
            deltaUnit += unitsOfTimeBetweenRepetitions.longValue();
        }
        Notification.show("Event " + eventNameArea.getValue() + " was created!");
        UI.getCurrent().getPage().setLocation("overview?" + "date=" + eventDatePicker.getValue().toString());
    }

    private void addEventHandler(long timeUnitDeltaFromOrigin, int timeUnitType) {
        Optional<String> err = Optional.empty();
        try {
            CalendarEvent event = getEventFromForm(timeUnitDeltaFromOrigin, timeUnitType);
            err = CalendarEventRepository.save(event);
        } catch (IllegalArgumentException e) {
            handleSqlException(e);
        }
        err.ifPresent(this::handleError);
    }

    private CalendarEvent getEventFromForm(long timeUnitDeltaFromOrigin, int timeUnitType) {
        LocalDate eventDate = null;

        if(timeUnitType == 0) {
            eventDate = eventDatePicker.getValue().plusDays(timeUnitDeltaFromOrigin);
        }
        else if(timeUnitType == 1) {
            eventDate = eventDatePicker.getValue().plusWeeks(timeUnitDeltaFromOrigin);
        }
        else if(timeUnitType == 2) {
            eventDate = eventDatePicker.getValue().plusMonths(timeUnitDeltaFromOrigin);
        }
        else {
            eventDate = eventDatePicker.getValue().plusYears(timeUnitDeltaFromOrigin);
        }

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
            err = CalendarEventRepository.update(eventIdString, getEventFromForm(0, 0));
        } catch (Exception e) {
            handleSqlException(e);
        } finally {
            err.ifPresent(this::handleError);
        }
    }

    private void initialiseButton(String eventIdString) {
        addEventButton = (eventIdString == null ? new Button("Add event") : new Button("Modify event"));
    }

    private void initialisePickers() {
        /* Picker of the new event start time */
        eventStartTimePicker = new TimePicker();
        eventStartTimePicker.setLabel("Choose or type in event start time (required):");
        eventStartTimePicker.setRequired(true);
        eventStartTimePicker.setValue(LocalTime.of(10, 0));

        /* Picker of the new event end time */
        eventEndTimePicker = new TimePicker();
        eventEndTimePicker.setLabel("Choose or type in event end time (required):");
        eventEndTimePicker.setRequired(true);
        eventEndTimePicker.setValue(LocalTime.of(12, 0));

        /* Picker of the new event date */
        eventDatePicker = new DatePicker();
        eventDatePicker.setLabel("Choose event date (required)");
        eventDatePicker.setRequired(true);
        eventDatePicker.setValue(LocalDate.now());
    }

    private void initialiseTextAreas() {
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
                "(do not change if you wish that the event occurs only once)");
        eventRepNumField.setMaxLength(4);
        eventRepNumField.setValue(Integer.toString(1));

        eventRepBreakField = new TextArea("Time between event repetitions - integer and D (day), W (week), " +
                                          "M (month) or Y (year). Examples: 13D, 5M.");
    }

    private void initialiseDivs() {
        eventDateTimeDiv = new Div();
        eventNameDescDiv = new Div();
        eventRepDiv = new Div();
        eventMiscDataDiv = new Div();

        eventDateTimeDiv.getElement().setProperty("innerHTML", "<p><b>Event date and time</b></p>");
        eventNameDescDiv.getElement().setProperty("innerHTML", "<p><b>Event name and description</b></p>");
        eventRepDiv.getElement().setProperty("innerHTML", "<p><b>Event repetitions</b></p>");
        eventMiscDataDiv.getElement().setProperty("innerHTML", "<p><b>Miscellaneous event data (tags, people, place)</b></p>");
    }


    private void initialiseLayouts() {
        eventDateAndTimeDivLayout = new HorizontalLayout();
        eventDateAndTimeLayout = new HorizontalLayout();
        eventNameAndDescriptionDivLayout = new HorizontalLayout();
        eventNameAndDescriptionLayout = new HorizontalLayout();
        eventRepetitionsDivLayout = new HorizontalLayout();
        eventRepetitionsLayout = new HorizontalLayout();
        eventMiscDataDivLayout = new HorizontalLayout();
        eventMiscDataLayout = new HorizontalLayout();

        eventDateAndTimeDivLayout.add(eventDateTimeDiv);
        eventDateAndTimeLayout.addAndExpand(eventDatePicker, eventStartTimePicker, eventEndTimePicker);
        eventNameAndDescriptionDivLayout.add(eventNameDescDiv);
        eventNameAndDescriptionLayout.addAndExpand(eventNameArea, eventDescriptionArea);
        eventRepetitionsDivLayout.add(eventRepDiv);
        eventRepetitionsLayout.addAndExpand(eventRepNumField, eventRepBreakField);
        eventMiscDataDivLayout.add(eventMiscDataDiv);
        eventMiscDataLayout.addAndExpand(tagsField, peopleField, eventPlaceField);
    }

    private void insertViewComponents() {
        add(eventDateAndTimeDivLayout, eventDateAndTimeLayout);
        add(eventNameAndDescriptionDivLayout, eventNameAndDescriptionLayout);
        add(eventRepetitionsDivLayout, eventRepetitionsLayout);
        add(eventMiscDataDivLayout, eventMiscDataLayout);
        add(addEventButton);
    }

    private void addCreateButtonListener(String eventIdString) {
        /* Listener for the Button object which is to add the event on click after
         *  checking correctness of event input data
         */
        addEventButton.addClickListener(e -> {
            /* Check for the case when event start time has not been provided.
             */
            if (eventStartTimePicker.getValue() == null) {
                Notification.show("Error: event start time has not been provided.");
            }
            /* Check for the case when event end time has not been provided.
             */
            else if (eventEndTimePicker.getValue() == null) {
                Notification.show("Error: event end time has not been provided.");
            }
            /*  Check for the case when both start and event end time were provided but according
             *  to the information given, event is to end before or the moment it starts.
             *  Prevent event adding by issuing an error message.
             */
            else if (eventStartTimePicker.getValue().compareTo(eventEndTimePicker.getValue()) >= 0) {
                Notification.show("Error: event start time later or just the event end time.");
            }
            /*  Check if no event date has been provided and issue an error message in such case
             */
            else if (eventDatePicker.getValue() == null) {
                Notification.show("Error: event date has not been provided.");
            } else {
                /* Initial checks passed, further actions can be taken
                 */
                handleAfterInitialCheck(eventIdString);
            }
        });
    }

    private void setValuesIfNecessary(String eventIdString) {
        if (eventIdString != null) {
            Optional<CalendarEvent> event = Optional.empty();
            try {
                event = CalendarEventRepository.findById(Integer.parseInt(eventIdString));
            } catch (IllegalArgumentException e) {
                handleSqlException(e);
            }
            if (event.isPresent()) {
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
            } else {
                Notification.show("Event with id " + eventIdString + " not found.");
            }
        }
    }

    public NewEventView() {
        addClassName("newevent-view");
        String eventIdString = VaadinService.getCurrentRequest().getParameter("event_id");

        initialiseButton(eventIdString);

        initialisePickers();
        initialiseTextAreas();
        initialiseDivs();
        initialiseLayouts();
        insertViewComponents();

        setValuesIfNecessary(eventIdString);
        addCreateButtonListener(eventIdString);
    }
}
