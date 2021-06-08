package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.Reminder;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.gio.calendar.persistance.ReminderRepository;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import javax.print.DocFlavor;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Route(value = "today_overview", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Today overview")
@CssImport("./views/overview/overview-view.css")
public class TodayOverview extends Div {
    private HorizontalLayout todayDateDivLayout;
    private HorizontalLayout overallEventsDivLayout;
    private HorizontalLayout overallEventsLayout;
    private HorizontalLayout remindersDivLayout;
    private HorizontalLayout remindersLayout;

    private Div todayDateDiv;
    private Div overallEventsDiv;
    private Div remindersDiv;

    private List<CalendarEvent> eventsList;
    private List<Reminder> remindersList;

    private List<HorizontalLayout> eventsLayouts;
    private List<HorizontalLayout> remindersLayouts;

    /**
     * Gets the info about days scheduled for current day.
     * @throws SQLException - in case of database exception while
     * retrieving event data
     */
    private void getTodayEventsInfo() throws SQLException {
        eventsList = CalendarEventRepository.findByDate(LocalDate.now());
    }

    /**
     * Gets the info about notes added for current day.
     * @throws SQLException - in case of database exception while
     * retrieving notes dta
     */
    private void getTodayRemindersInfo() throws SQLException {
        remindersList = ReminderRepository.findByDate(LocalDate.now());

        /* Display only reminders that are set to time not earlier than 30 minutes from now */
        remindersList.removeIf(r -> LocalTime.now().isAfter(r.getReminderTime().plusMinutes(30)));
    }

    /**
     * Initialises the Div components.
     */
    private void initialiseDivs() {
        todayDateDiv = new Div();
        remindersDiv = new Div();
        overallEventsDiv = new Div();

        todayDateDiv.getElement().setProperty("innerHTML", "<p><b>Today date is " + LocalDate.now() + "</b></p>");


        try {
            getTodayEventsInfo();
            getTodayRemindersInfo();

        }
        catch(SQLException e) {
            Notification.show("Error while collecting data from database...");
        }
        finally {
            if(!eventsList.isEmpty()) {
                overallEventsDiv.getElement().setProperty("innerHTML", "<p><b>You have " + eventsList.size() +
                                                          " event(s) scheduled for today</b></p>");
            }
            else {
                overallEventsDiv.getElement().setProperty("innerHTML", "<p><b>You don't have any events scheduled for today</b></p>");
            }

            if(!remindersList.isEmpty()) {
                remindersDiv.getElement().setProperty("innerHTML", "<p><b>You have " + remindersList.size() +
                                                      " reminders</b></p>");
            }
            else {
                remindersDiv.getElement().setProperty("innerHTML", "<p><b>You have no reminders</b></p>");
            }
        }
    }

    /**
     * Initialises the overview layouts.
     */
    private void initialiseLayouts() {
        todayDateDivLayout = new HorizontalLayout();
        overallEventsDivLayout = new HorizontalLayout();
        overallEventsLayout = new HorizontalLayout();
        remindersDivLayout = new HorizontalLayout();
        remindersLayout = new HorizontalLayout();

        todayDateDivLayout.add(todayDateDiv);
        overallEventsDivLayout.add(overallEventsDiv);
        remindersDivLayout.add(remindersDiv);

        for(CalendarEvent e: eventsList) {
            HorizontalLayout forCurrentEvent = new HorizontalLayout();

            forCurrentEvent.add(new Label("Event " + e.getEventName() + " starting at " + e.getEventStartTimeString() +
                                (e.getEventEndTimeString() == "" ? "" : (", ending at " + e.getEventEndTimeString()))));

            eventsLayouts.add(forCurrentEvent);
        }

        for(Reminder r: remindersList) {
            HorizontalLayout forCurrentReminder = new HorizontalLayout();

            Label reminderLabel = new Label("Reminder set to time " +
                                            r.getReminderTime() +
                                            ", content: " +
                                            r.getReminderContent());

            reminderLabel.getStyle().set("color", "red");
            reminderLabel.getStyle().set("font-weight", "bold");

            forCurrentReminder.add(reminderLabel);
            forCurrentReminder.add();

            remindersLayouts.add(forCurrentReminder);
        }
    }

    /**
     * Inserts the view components.
     */
    private void insertViewComponents() {
        add(todayDateDivLayout);
        add(overallEventsDivLayout);

        for(HorizontalLayout layout: eventsLayouts) {
            add(layout);
        }

        add(remindersDivLayout);

        for(HorizontalLayout layout: remindersLayouts) {
            add(layout);
        }
    }

    /**
     * Overview constructor.
     * Initialises the components (layouts, divs, ...) and adds
     * them to the view.
     */
    public TodayOverview() {
        eventsList = new ArrayList<>();
        remindersList = new ArrayList<>();

        eventsLayouts = new ArrayList<>();
        remindersLayouts = new ArrayList<>();

        initialiseDivs();
        initialiseLayouts();
        insertViewComponents();
    }
}
