package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.Person;
import com.gio.calendar.models.Tag;
import com.gio.calendar.persistance.CalendarEventRepo;
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

    private final HorizontalLayout eventDateLayout;
    private final HorizontalLayout eventDescriptionLayout;
    private final HorizontalLayout eventNameLayout;

    private final HorizontalLayout eventStartTimeLayout;
    private final HorizontalLayout eventEndTimeLayout;
    private final HorizontalLayout tagsFieldLayout;
    private final HorizontalLayout peopleFieldLayout;
    private final HorizontalLayout eventPlaceFieldLayout;


    private void handleSqlException(Exception e) {
        Notification.show("SQLException occured. Event has not been added.");
        Notification.show("SQLException occurred: " + e.getMessage());
        Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
    }

    private void addEventHandler() {
        try {
            CalendarEvent event = new CalendarEvent(
                    eventNameArea.getValue(),
                    eventDescriptionArea.getValue(),
                    eventDatePicker.getValue(),
                    eventStartTimePicker.getValue(),
                    eventEndTimePicker.getValue(),
                    tagsField.getValue(),
                    eventPlaceField.getValue(),
                    peopleField.getValue());
            CalendarEventRepo.save(event);
        }
        catch(IllegalArgumentException e) {
            handleSqlException(e);
        }
    }

    private void modifyEventHandler(String eventIdString) {
    	boolean operationSuccess = true;
    	
    	try {
    		CalendarEventRepo.deleteById(Integer.parseInt(eventIdString));
    	}
    	catch(IllegalArgumentException e) {
    		operationSuccess = false;
    		handleSqlException(e);
    	}
        if(operationSuccess) {
            addEventHandler();
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

        /* Layouts creating */
        eventDateLayout = new HorizontalLayout();
        eventDescriptionLayout = new HorizontalLayout();
        eventNameLayout = new HorizontalLayout();
        eventStartTimeLayout = new HorizontalLayout();
        eventEndTimeLayout = new HorizontalLayout();
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
        tagsFieldLayout.addAndExpand(tagsField);
        peopleFieldLayout.addAndExpand(peopleField);
        eventPlaceFieldLayout.addAndExpand(eventPlaceField);

        /* Add all layouts */
        add(eventDateLayout, eventNameLayout, eventDescriptionLayout,
            eventStartTimeLayout, eventEndTimeLayout, tagsFieldLayout,
                peopleFieldLayout, eventPlaceFieldLayout, addEventButton);
        
        if(eventIdString != null) {
            Optional<CalendarEvent> event = Optional.empty();
            try {
                event = CalendarEventRepo.findById(Integer.parseInt(eventIdString));
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
            	if(eventIdString == null) {
	                addEventHandler();
	                Notification.show("Event " + eventNameArea.getValue() + " was created!");
            	}
            	else {
            		modifyEventHandler(eventIdString);
            		Notification.show("Event successfully modified!");
            	}
                clearForm();
                
            }
        });
    }
}
