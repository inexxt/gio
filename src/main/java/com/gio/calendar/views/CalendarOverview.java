package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.Person;
import com.gio.calendar.models.Tag;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.dialog.*;
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
    private static LocalDate dateToSet = null;
    private static boolean forceDatePickerValue = false;

    private static final int DAILY_EVENTS_LIMIT = 120;

    private final DatePicker targetDatePicker;
    private final HorizontalLayout datePickerLayout;
    private final VerticalLayout[] infoLayouts;
    private List<CalendarEvent> eventsList;

    /*  Collects information from database about events that user has planned for the day on
     *  the date chosen in targetDatePicker
     */
    private void getEventsInfo() throws SQLException {
        LocalDate targetDateStart = targetDatePicker.getValue();
        eventsList = CalendarEventRepository.findByDate(targetDateStart);
    }

    /* Sets events info for display on page.
     * Returns the index (in infoLayouts array) of first non-occupied layout
     * that can (as well as successive layouts in array) be further filled
     * with informations about tasks scheduled for specified day
     */
    private int setEventsInfo() {
        int eventIndex = 0;
        int eventNo = 1;

        eventsList.sort(Comparator.comparing(CalendarEvent::getEventStartTime));

        for (CalendarEvent e : eventsList) {
            /* Event tag displaying information in format "Event + n" where n is
             * ordinal number of event in specified day (order: 1, 2, ...)
             */
            Label eventTag = new Label("Event " + eventNo);
            eventTag.setWidth(null);
            eventTag.setHeight("10px");
            eventTag.getStyle().set("font-weight", "bold");

            infoLayouts[eventIndex].add(eventTag);

            /* Array of break labels to be input between event data fields */
            Label[] breakLabels = new Label[7];

            /* Array of text labels to display specified event data
             * (event name, description, start time and end time)
             */
            Label[] textLabels = new Label[7];

            /* Set up break labels
             */
            for (int i = 0; i < 7; ++i) {
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
            textLabels[4] = new Label("Tags: " + Tag.tagsToString(e.getEventTags()));
            textLabels[5] = new Label("Place: " + e.getEventPlace());
            textLabels[6] = new Label("Guests: " + Person.peopleToString(e.getEventPeople()));
            /* Set width and height of text labels and add both break and text labels
             * to the display
             */
            for (int i = 0; i < 7; ++i) {
                textLabels[i].setWidth("30%");
                textLabels[i].setHeight("10px");

                infoLayouts[eventIndex].add(textLabels[i]);
                infoLayouts[eventIndex].add(breakLabels[i]);
            }

            /* Layout for displaying buttons which are to handle available actions
             * concerning the specified event:
             * Event data modification
             * Event deletion
             */
            HorizontalLayout eventActionsLayout = new HorizontalLayout();

            Button eventModificationButton = new Button("Modify event data");
            Button eventDeleteButton = new Button("Delete event");

            eventActionsLayout.add(eventModificationButton, eventDeleteButton);
            infoLayouts[eventIndex].add(eventActionsLayout);

            eventModificationButton.addClickListener(v -> {
                UI.getCurrent().getPage().setLocation("new_event" + "?" + "event_id=" + e.getEventId());
            });

            addDeleteConfirmation(eventDeleteButton, e);
            eventIndex++;
            eventNo++;
        }

        return eventIndex;
    }

    private void addDeleteConfirmation(Button eventDeleteButton, CalendarEvent e) {
        eventDeleteButton.addClickListener(v -> {
            Dialog deleteDialog = new Dialog();

            /* Layouts with following purposes:
             * queryLayout - stores Label with query text
             * buttonsLayout - stores Buttons which execute further actions
             */
            HorizontalLayout queryLayout = new HorizontalLayout();
            HorizontalLayout buttonsLayout = new HorizontalLayout();

            Label deleteConfirmationQuery = new Label("Please confirm operation");
            Button eventDeleteConfirmation = new Button("Confirm event deletion");
            Button eventDeleteCancellation = new Button("Cancel event deletion");

            queryLayout.add(deleteConfirmationQuery);
            buttonsLayout.add(eventDeleteCancellation, eventDeleteConfirmation);

            deleteDialog.add(queryLayout, buttonsLayout);

            deleteDialog.setVisible(true);
            deleteDialog.open();

            eventDeleteCancellation.addClickListener(w -> {
                deleteDialog.close();
            });

            eventDeleteConfirmation.addClickListener(w -> {
                /* Flag to indicate whether event deletion was successful
                 */
                boolean okDeletion = true;
                /* Store current date stored in targetDatePicker to set it as
                 * targetDatePicker value after the page refresh which occurs
                 * after successful event deletion
                 */
                LocalDate saveDate = targetDatePicker.getValue();

                try {
                    CalendarEventRepository.deleteById(e.getEventId());
                } catch (IllegalArgumentException ex) {
                    /* Mark deletion as unsuccessful and issue proper notification informing
                     * about the exception that has occurred
                     */
                    okDeletion = false;
                    Notification.show("Exception occured. Event has not been deleted.");
                } finally {
                    /* Proceed with page update on successful deletion of event data
                     * from the database
                     */
                    if (okDeletion) {
                        Notification.show("Event has been successfully deleted.");
                        /* Update class data with values that will force the page to display
                         * current date (the one in targetDatePicker) after the page has been
                         * reloaded
                         */
                        dateToSet = saveDate;
                        forceDatePickerValue = true;
                        UI.getCurrent().getPage().reload();
                    }
                }
                deleteDialog.close(); // Close the dialog
            });
        });
    }

    private void dateChangeHandler() throws SQLException, IOException, ClassNotFoundException {
        getEventsInfo();

        /*  Clear layouts which display information about the events */
        for (int i = 0; i < 2 * DAILY_EVENTS_LIMIT; i++) {
            /* Check if current layout is used (contains at least one component) and
             * clear it then
             */
            if (infoLayouts[i].getComponentCount() > 0) {
                infoLayouts[i].removeAll();
            }
            /* Break from the loop as remaining layouts are not used at all
             */
            else if (infoLayouts[i].getComponentCount() == 0) {
                break;
            }
        }

        int tasksInfoStartingIndex = 1;

        /* Display appropriate message instead of events data when no events are scheduled
         * for the specified day
         */
        if (eventsList.isEmpty()) {
            Label noEventsInfoLabel = new Label("No events on specified date.");
            noEventsInfoLabel.setWidth(null);
            noEventsInfoLabel.setHeight("5px");

            infoLayouts[0].add(noEventsInfoLabel);
        }
        /* Display information about the tasks scheduled for specified day and obtain
         * (return value) the index of first non-used layout that can store tasks data
         */
        else {
            tasksInfoStartingIndex = setEventsInfo();
        }
    }

    public CalendarOverview() {
        addClassName("overview-view");

        eventsList = new ArrayList<>();

        Notification.show("Establishing connection to db and initializing data");
        Notification.show("Connection established");

        datePickerLayout = new HorizontalLayout();

        targetDatePicker = new DatePicker();
        targetDatePicker.setLabel("Choose date to view");

        targetDatePicker.addValueChangeListener(e -> {
            if (targetDatePicker.getValue() != null) {
                eventsList.clear();

                try {
                    dateChangeHandler();
                } catch (SQLException | ClassNotFoundException | IOException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        datePickerLayout.addAndExpand(targetDatePicker);
        add(datePickerLayout);

        infoLayouts = new VerticalLayout[2 * DAILY_EVENTS_LIMIT];

        for (int i = 0; i < 2 * DAILY_EVENTS_LIMIT; ++i) {
            infoLayouts[i] = new VerticalLayout();
            add(infoLayouts[i]);
        }

        if (forceDatePickerValue) {
            targetDatePicker.setValue(dateToSet);
            /* Now after the value has been changed, the valueChangeListener associated
             * with the targetDatePicker object will do its action
             */

            /* Restore original flags values for further possible deletions of events
             */
            forceDatePickerValue = false;
            dateToSet = null;
        }
    }
}
