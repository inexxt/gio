package com.example.application.views.helloworld;

import com.example.application.utilities.calendar.calendarevent.CalendarEvent;
import com.example.application.utilities.database.ConnectionManager;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dependency.CssImport;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;


@Route(value = "hello", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Calendar overview")
@CssImport("./views/helloworld/hello-world-view.css")
public class CalendarOverview extends Div {
    private static final int DAILY_EVENTS_LIMIT = 1440;

    private final DatePicker targetDatePicker;
	private final HorizontalLayout datePickerLayout;
    private final HorizontalLayout[] eventInfoLayouts;

    private Connection databaseConn;

    private final ArrayList<CalendarEvent> eventsList;

    /*  Collects information from database about events that user has planned for the day on
     *  the date chosen in targetDatePicker
     */
    private void getEventsInfo() {
        /* TODO: CONNECTION TO DATABASE AND GRABBING INFORMATION ABOUT EVENTS ON SPECIFIC DAY */
    }

    private void dateChangeHandler() {
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

        eventsList.sort(Collections.reverseOrder());

        for(CalendarEvent e: eventsList) {
            eventInfoLayouts[eventIndex].add(new Text(e.toString()));

            eventIndex++;
        }

        /* No longer needed, clear */
        eventsList.clear();
    }
    public CalendarOverview() {
        addClassName("hello-world-view");

        eventsList = new ArrayList<>();

        try {
            databaseConn = ConnectionManager.getNewConnection();
        }
        catch(IOException e) {
            Notification.show("IOException occurred: " + e.getStackTrace());
        }
        catch(SQLException e) {
            Notification.show("SQLException occurred: " + e.getStackTrace());
        }

        datePickerLayout = new HorizontalLayout();

        targetDatePicker = new DatePicker();
        targetDatePicker.setLabel("Choose date to view");

        targetDatePicker.addValueChangeListener(e -> {
            if(targetDatePicker.getValue() != null) {
                dateChangeHandler();
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
