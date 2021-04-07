package com.gio.calendar.views.events;

import com.gio.calendar.utilities.calendar.tag.Tag;
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
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.dependency.CssImport;


import java.sql.*;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

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

    private final HorizontalLayout eventDateLayout;
    private final HorizontalLayout eventDescriptionLayout;
    private final HorizontalLayout eventNameLayout;

    private final HorizontalLayout eventStartTimeLayout;
    private final HorizontalLayout eventEndTimeLayout;
    private final HorizontalLayout tagsFieldLayout;

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
        ResultSet res = pstmt.getGeneratedKeys();
        List<Tag> tags = new ArrayList<Tag>();
        tags = Arrays.stream(tagsField.getValue().split(",")).map(Tag::new).collect(Collectors.toList());
        while(res.next()) {
            for (Tag t : tags) {
                String sql_task = "INSERT INTO event_tags(event, tag) VALUES(?, ?)";
                PreparedStatement pstmt_task = conn.prepareStatement(sql_task);
                pstmt_task.setString(1, res.getString(1));
                pstmt_task.setString(2, t.toString());
                pstmt_task.executeUpdate();
            }
        }
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
    
    private void removeEventFromDatabase(String eventIdString) throws SQLException,
    																  IOException,
    																  ClassNotFoundException {
	    Connection conn = ConnectionManager.getConnection();
	    String sql = "DELETE FROM events WHERE id = ?";
    	PreparedStatement pstmt = conn.prepareStatement(sql);
    	pstmt.setInt(1, Integer.parseInt(eventIdString));
    	pstmt.execute();
    }
    
    private void modifyEventHandler(String eventIdString) {
    	boolean operationSuccess = true;
    	
    	try {
    		removeEventFromDatabase(eventIdString);
    	}
    	catch(SQLException e) {
    		operationSuccess = false;
    		Notification.show("SQLException occured. Event has not been modified.");
    	}
    	catch(IOException e) {
    		operationSuccess = false;
    		Notification.show("IOException occured. Event has not been modified.");
    	}
    	catch(ClassNotFoundException e) {
    		operationSuccess = false;
    		Notification.show("ClassNotFoundException occured. Event has not been modified.");
    	}
    	finally {
    		if(operationSuccess) {
    			addEventHandler();
    		}
    	}
    }
    
    private void clearForm() {
        eventNameArea.clear();
        eventDescriptionArea.clear();
        eventDatePicker.clear();
        eventEndTimePicker.clear();
        eventStartTimePicker.clear();
        tagsField.clear();
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


        /* Layouts creating */
        eventDateLayout = new HorizontalLayout();
        eventDescriptionLayout = new HorizontalLayout();
        eventNameLayout = new HorizontalLayout();
        eventStartTimeLayout = new HorizontalLayout();
        eventEndTimeLayout = new HorizontalLayout();
        tagsFieldLayout = new HorizontalLayout();

        /* Button for confirming new event add operation */
        addEventButton = (eventIdString == null ? new Button("Add event") : new Button("Modify event"));

        /* Enrich layouts with created components */
        eventDateLayout.addAndExpand(eventDatePicker);
        eventDescriptionLayout.addAndExpand(eventDescriptionArea);
        eventNameLayout.addAndExpand(eventNameArea);
        eventStartTimeLayout.addAndExpand(eventStartTimePicker);
        eventEndTimeLayout.addAndExpand(eventEndTimePicker);
        tagsFieldLayout.addAndExpand(tagsField);

        /* Add all layouts */
        add(eventDateLayout, eventNameLayout, eventDescriptionLayout,
            eventStartTimeLayout, eventEndTimeLayout, tagsFieldLayout, addEventButton);
        
        if(eventIdString != null) {
        	try {
	        	String sql = "select id, name, desc, event_start, event_end from events where id = ?;";
	            PreparedStatement pstmt = ConnectionManager.getConnectionManager().getConn().prepareStatement(sql);
	
	            pstmt.setInt(1, Integer.parseInt(eventIdString));
	
	            ResultSet rs = pstmt.executeQuery();
	            while(rs.next()) {
	                eventNameArea.setValue(rs.getString("name"));
	                eventDescriptionArea.setValue(rs.getString("desc"));
	                
	                LocalDateTime eventStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getInt("event_start")*1000L),
	                        TimeZone.getDefault().toZoneId());
	
	                LocalDateTime eventEnd = LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getInt("event_end")*1000L),
	                        TimeZone.getDefault().toZoneId());
	
	                LocalTime startTimeLocal = LocalTime.of(eventStart.getHour(),
	                        eventStart.getMinute());
	
	                LocalTime endTimeLocal = LocalTime.of(eventEnd.getHour(),
	                        eventEnd.getMinute());
	
	                LocalDate eventDate = LocalDate.of(eventStart.getYear(),
	                        eventStart.getMonthValue(),
	                        eventStart.getDayOfMonth());
	                
	                eventStartTimePicker.setValue(startTimeLocal);
	                eventEndTimePicker.setValue(endTimeLocal);
	                
	                eventDatePicker.setValue(eventDate);
	            }
        	}
        	catch(SQLException e) {
        		Notification.show("SQLException occured.");
        	}
        	catch(ClassNotFoundException e) {
        		Notification.show("ClassNotFoundException occured.");
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
