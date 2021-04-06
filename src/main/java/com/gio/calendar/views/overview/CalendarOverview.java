package com.gio.calendar.views.overview;

import com.gio.calendar.utilities.calendar.calendarevent.CalendarEvent;
import com.gio.calendar.utilities.database.ConnectionManager;
import com.gio.calendar.views.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dependency.CssImport;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.util.*;


@Route(value = "overview", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Calendar overview")
@CssImport("./views/overview/overview-view.css")
public class CalendarOverview extends Div {
    private static final int DAILY_EVENTS_LIMIT = 120;

//    private final Button button;
    private final DatePicker targetDatePicker;
	private final HorizontalLayout datePickerLayout;
    private final VerticalLayout[] eventInfoLayouts;

    private final ArrayList<CalendarEvent> eventsList;

    /*  Collects information from database about events that user has planned for the day on
     *  the date chosen in targetDatePicker
     */
    private void getEventsInfo() throws SQLException, IOException, ClassNotFoundException {
        String sql = "select name, desc, event_start, event_end from events where (event_start > ?) and (event_start < ?);";
        PreparedStatement pstmt = ConnectionManager.getConnectionManager().getConn().prepareStatement(sql);
        
        LocalDateTime targetDateStart = targetDatePicker.getValue().atStartOfDay();
        LocalDateTime targetDateEnd = targetDatePicker.getValue().atStartOfDay().plusDays(1).minusSeconds(1);

        pstmt.setInt(1, ((int) (Timestamp.valueOf(targetDateStart).getTime() / 1000L)));
        pstmt.setInt(2, ((int) (Timestamp.valueOf(targetDateEnd).getTime() / 1000L)));

        ResultSet rs = pstmt.executeQuery();
        
        while(rs.next()) {        	
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
            
            String tags = ""; // empty for now
            CalendarEvent event = new CalendarEvent(
                    rs.getString("name"),
                    rs.getString("desc"),
                    eventDate,
                    startTimeLocal,
                    endTimeLocal,
                    tags
            );
            eventsList.add(event);
        }
    }

    private void dateChangeHandler() throws SQLException, IOException, ClassNotFoundException {
        getEventsInfo();

        /*  Clear layouts which display information about the events */
        for(int i = 0; i < DAILY_EVENTS_LIMIT; i++) {
            /* Check if current layout is used (contains at least one component) and
             * clear it then
             */
            if(eventInfoLayouts[i].getComponentCount() > 0) {
                eventInfoLayouts[i].removeAll();
            }
            /* Break from the loop as remaining layouts are not used at all
             */
            else {
                break;
            }
        }
        
        /* Display appropriate message instead of events data when no events are scheduled
         * for the specified day
         */
        if(eventsList.isEmpty()) {
        	Label noEventsInfoLabel = new Label("No events on specified date.");
        	noEventsInfoLabel.setWidth(null);
        	noEventsInfoLabel.setHeight("5px");
        	
        	eventInfoLayouts[0].add(noEventsInfoLabel); 

            return;
        }

        int eventIndex = 0;
        int eventNo = 1;
        
        eventsList.sort(Comparator.comparing(CalendarEvent::getStart));
        
        for(CalendarEvent e: eventsList) {
        	/* Event tag displaying information in format "Event + n" where n is
        	 * ordinal number of event in specified day (order: 1, 2, ...)
        	 */
        	Label eventTag = new Label("Event " + eventNo);
        	eventTag.setWidth(null);
        	eventTag.setHeight("10px");
        	eventTag.getStyle().set("font-weight", "bold");
        	
        	eventInfoLayouts[eventIndex].add(eventTag);
        	
        	/* Array of break labels to be input between event data fields */
        	Label[] breakLabels = new Label[4];
        	
        	/* Array of text labels to display specified event data
        	 * (event name, description, start time and end time)
        	 */
        	Label[] textLabels = new Label[4];
        	
        	/* Set up break labels
        	 */
        	for(int i = 0; i < 4; ++i) {
        		breakLabels[i] = new Label("");
        		breakLabels[i].setWidth(null);
        		breakLabels[i].setHeight("0.1px");
        	}
        	
        	/* Set up text labels
        	 */
        	textLabels[0] = new Label("Name: " + e.getEventName());
        	textLabels[1] = new Label("Description: " + e.getEventDescription());
        	textLabels[2] = new Label("Start time: " + e.getEventStartTimeString());
        	textLabels[3] = new Label("End time: " + e.getEventEndTimeString());
        	
        	/* Set width and height of text labels and add both break and text labels
        	 * to the display
        	 */
        	for(int i = 0; i < 4; ++i) {
        		textLabels[i].setWidth("30%");
        		textLabels[i].setHeight("10px");
        		
        		eventInfoLayouts[eventIndex].add(textLabels[i]);
        		eventInfoLayouts[eventIndex].add(breakLabels[i]);
        	}

            eventIndex++;
            eventNo++;
        }

        /* No longer needed, clear */
        eventsList.clear();
    }

    public CalendarOverview() {
        addClassName("overview-view");

        eventsList = new ArrayList<>();

        try {
            Notification.show("Establishing connection to db and initializing data");
            ConnectionManager.initialize();
            Notification.show("Connection established");
        }
        catch(IOException e) {
            Notification.show("IOException occurred: " + e.getMessage());
        }
        catch(SQLException | ClassNotFoundException e) {
            Notification.show("SQLException occurred: " + e.getMessage());
        }

        datePickerLayout = new HorizontalLayout();

        targetDatePicker = new DatePicker();
        targetDatePicker.setLabel("Choose date to view");

        targetDatePicker.addValueChangeListener(e -> {
            if(targetDatePicker.getValue() != null) {
                try {
                    dateChangeHandler();
                } catch (SQLException | ClassNotFoundException | IOException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        datePickerLayout.addAndExpand(targetDatePicker);
        add(datePickerLayout);

        eventInfoLayouts = new VerticalLayout[DAILY_EVENTS_LIMIT];

        for(int i = 0; i < DAILY_EVENTS_LIMIT; ++i) {
            eventInfoLayouts[i] = new VerticalLayout();
            add(eventInfoLayouts[i]);
        }
    }
}
