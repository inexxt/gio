package com.gio.calendar.views.overview;

import com.gio.calendar.utilities.calendar.calendarevent.CalendarEvent;
import com.gio.calendar.utilities.database.ConnectionManager;
import com.gio.calendar.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dependency.CssImport;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;


@Route(value = "overview", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Calendar overview")
@CssImport("./views/overview/overview-view.css")
public class CalendarOverview extends Div {
    private static final int DAILY_EVENTS_LIMIT = 120;

//    private final Button button;
    private final DatePicker targetDatePicker;
	private final HorizontalLayout datePickerLayout;
    private final HorizontalLayout[] eventInfoLayouts;

    private Connection databaseConn;

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
        System.out.println("Executing select: " + pstmt.toString()); // TODO remove
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            Date startTime = new Date(rs.getInt("event_start"));
            Date endTime = new Date(rs.getInt("event_end"));
            String timeZone = ZoneId.systemDefault().toString();
            LocalTime startTimeLocal = LocalTime.ofInstant(startTime.toInstant(), ZoneId.of(timeZone));
            LocalTime endTimeLocal = LocalTime.ofInstant(endTime.toInstant(), ZoneId.of(timeZone));
            LocalDate eventDate = LocalDate.ofInstant(startTime.toInstant(), ZoneId.of(timeZone));
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

        int eventIndex = 0;
        eventsList.sort(Comparator.comparing(CalendarEvent::getStart));
        for(CalendarEvent e: eventsList) {
            eventInfoLayouts[eventIndex].add(new Text(e.toString()));
            eventIndex++;
        }

        /* No longer needed, clear */
        eventsList.clear();
    }

    public CalendarOverview() {
        addClassName("overview-view");

        eventsList = new ArrayList<>();

        try {
            Notification.show("Establishing connection to db and initializing data");
            ConnectionManager.inititialize();
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

        eventInfoLayouts = new HorizontalLayout[DAILY_EVENTS_LIMIT];

        for(int i = 0; i < DAILY_EVENTS_LIMIT; ++i) {
            eventInfoLayouts[i] = new HorizontalLayout();
            add(eventInfoLayouts[i]);
        }
    }
}
