package com.example.application.views.helloworld;

import com.example.application.utilities.calendar.calendarevent.CalendarEvent;
import com.example.application.utilities.database.ConnectionManager;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dependency.CssImport;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;


@Route(value = "hello", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Calendar overview")
@CssImport("./views/helloworld/hello-world-view.css")
public class CalendarOverview extends Div {

	private Button previousDay;
	private Button nextDay;

	private HorizontalLayout datePickerLayout;
	private HorizontalLayout dateNotesLayout;
	private HorizontalLayout dateEventsLayout;

	private HorizontalLayout dateOverview;
	private TextArea dateTextArea;
    private Connection databaseConn;

    private DatePicker targetDatePicker;

    private ArrayList<CalendarEvent> eventsList;

    public CalendarOverview() {
        addClassName("hello-world-view");

        try {
            databaseConn = ConnectionManager.getNewConnection();
        }
        catch(IOException e) {
            Notification.show("IOException occured: " + e.getStackTrace());
        }
        catch(SQLException e) {
            Notification.show("SQLException occured: " + e.getStackTrace());
        }

        datePickerLayout = new HorizontalLayout();

        targetDatePicker = new DatePicker();
        targetDatePicker.setValue(LocalDate.now());
        targetDatePicker.setLabel("Choose date to view");
        targetDatePicker.setWidth("30%");

        targetDatePicker.addValueChangeListener(e -> {
            if(targetDatePicker.getValue() != null) {
                Notification.show("Not null date test...");
                Notification.show(targetDatePicker.getValue().toString());

            }
        });

        datePickerLayout.addAndExpand(targetDatePicker);
        add(datePickerLayout);

        dateEventsLayout = new HorizontalLayout();

        add(dateEventsLayout);
        /* May come handy in some tim
        previousDay.addClickListener(e -> {
        	cal.add(Calendar.DATE, -1);

            dateTextArea.setLabel(Objects.requireNonNull(CalendarManager.getCurrentCalendarDate(cal)));
        });

        nextDay.addClickListener(e -> {
        	cal.add(Calendar.DATE, 1);

            dateTextArea.setLabel(Objects.requireNonNull(CalendarManager.getCurrentCalendarDate(cal)));
        });
         */
    }
}
