package com.gio.calendar.views.events;

import com.gio.calendar.utilities.database.ConnectionManager;
import com.gio.calendar.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.dependency.CssImport;


import java.sql.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

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

    private final Button addEventButton;

    private final DatePicker eventDatePicker;

    private final TimePicker eventStartTimePicker;
    private final TimePicker eventEndTimePicker;

    private final TextArea eventNameArea;
    private final TextArea eventDescriptionArea;

    private final HorizontalLayout eventDateLayout;
    private final HorizontalLayout eventDescriptionLayout;
    private final HorizontalLayout eventNameLayout;

    private final HorizontalLayout eventStartTimeLayout;
    private final HorizontalLayout eventEndTimeLayout;

    private void addEventToDatabase() throws SQLException, IOException, ClassNotFoundException {
        LocalDate eventDate = eventDatePicker.getValue();
        
        LocalDateTime eventStart = LocalDateTime.of(eventDate.getYear(), 
        										    eventDate.getMonthValue(), 
        										    eventDate.getDayOfMonth(),
        										    eventStartTimePicker.getValue().getHour(), 
        										    eventStartTimePicker.getValue().getMinute());

        LocalDateTime eventEnd = LocalDateTime.of(eventDate.getYear(), 
        										  eventDate.getMonthValue(), 
        										  eventDate.getDayOfMonth(),
        										  eventEndTimePicker.getValue().getHour(), 
        										  eventEndTimePicker.getValue().getMinute());
                
        Connection conn = ConnectionManager.getConnection();
        String sql = "INSERT INTO events(name, desc, event_start, event_end) VALUES(?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, (eventNameArea.getValue() != null ? 
        					eventNameArea.getValue() : "Event name not provided."));
        
        pstmt.setString(2, (eventDescriptionArea.getValue() != null ? 
        					eventDescriptionArea.getValue() : "Event description not provided."));
        
        pstmt.setInt(3, (int) (Timestamp.valueOf(eventStart).getTime() / 1000L));
        pstmt.setInt(4, (int) (Timestamp.valueOf(eventEnd).getTime() / 1000L));
        pstmt.executeUpdate();
    }

    private void addEventHandler() {
        try {
            addEventToDatabase();
        }
        catch(SQLException e) {
            Notification.show("SQLException occured. Event has not been added.");
            Notification.show("SQLException occurred: " + e.getMessage());
            Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
        }
        catch(IOException e) {
            Notification.show("IOException occured.");
        } catch (ClassNotFoundException e) {
            Notification.show("JDBC error: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        eventNameArea.clear();
        eventDescriptionArea.clear();
        eventDatePicker.clear();
        eventEndTimePicker.clear();
        eventStartTimePicker.clear();
    }

    public NewEventView() {
        addClassName("newevent-view");
        
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

        /* Layouts creating */
        eventDateLayout = new HorizontalLayout();
        eventDescriptionLayout = new HorizontalLayout();
        eventNameLayout = new HorizontalLayout();
        eventStartTimeLayout = new HorizontalLayout();
        eventEndTimeLayout = new HorizontalLayout();

        /* Button for confirming new event add operation */
        addEventButton = new Button("Add event");

        /* Enrich layouts with created components */
        eventDateLayout.addAndExpand(eventDatePicker);
        eventDescriptionLayout.addAndExpand(eventDescriptionArea);
        eventNameLayout.addAndExpand(eventNameArea);
        eventStartTimeLayout.addAndExpand(eventStartTimePicker);
        eventEndTimeLayout.addAndExpand(eventEndTimePicker);

        /* Add all layouts */
        add(eventDateLayout, eventNameLayout, eventDescriptionLayout,
            eventStartTimeLayout, eventEndTimeLayout, addEventButton);

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
                addEventHandler();
                Notification.show("Event " + eventNameArea.getValue() + " was created!");
                clearForm();
            }
        });
    }
}
