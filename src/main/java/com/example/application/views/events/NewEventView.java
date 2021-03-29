package com.example.application.views.events;

import com.example.application.utilities.database.ConnectionManager;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;


import java.sql.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@Route(value = "new_event", layout = MainView.class)
@PageTitle("New event")
@CssImport("./views/newevent/newevent-view.css")
public class NewEventView extends Div {
    /**
     * Maximum number of characters that event's name can contain
     */
    private static final Integer EVENT_NAME_CHARACTERS_LIMIT = 150;

    /**
     * Maximum number of characters that event's description can contain
     */
    private static final Integer EVENT_DESCRIPTION_CHARACTERS_LIMIT = 1000;

    /**
     * Used to determine whether event name should be provided as new event's data
     */
    private static final boolean EVENT_NAME_REQUIRED = true;

    private final Button addEventButton;

    private final DatePicker eventDatePicker;

    private final TextArea eventNameArea;
    private final TextArea eventDescriptionArea;

    private final HorizontalLayout eventDateLayout;
    private final HorizontalLayout eventDescriptionLayout;
    private final HorizontalLayout eventNameLayout;

    private void addEventToDatabase() throws SQLException, IOException {
        Connection conn;
        Statement stat;

        try {
            conn = ConnectionManager.getNewConnection();
            stat = conn.createStatement();

            /* TO DO: Insert appropriate values into the table */
        }
        catch(IOException | SQLException e) {
            throw e;
        }
    }

    private void addEventHandler() {
        try {
            addEventToDatabase();
        }
        catch(SQLException e) {
            Notification.show("SQLException occured. Event has not been added.");
        }
        catch(IOException e) {
            Notification.show("IOException occured.");
        }
    }

    public NewEventView() {
        addClassName("newevent-view");

        /* Picker of the new event's date */
        eventDatePicker = new DatePicker();
        eventDatePicker.setLabel("Choose event date");

        /* Text area for new event's name */
        eventNameArea = new TextArea("Event name " + (EVENT_NAME_REQUIRED ? "(required)" : "")  +
                                     ". Maximum length: " + EVENT_NAME_CHARACTERS_LIMIT.toString());

        eventNameArea.setMaxLength(EVENT_NAME_CHARACTERS_LIMIT);
        eventNameArea.setRequired(EVENT_NAME_REQUIRED);

        /* Text area for new event's description */
        eventDescriptionArea = new TextArea("Event description (optional). Maximum length: " +
                                            EVENT_DESCRIPTION_CHARACTERS_LIMIT.toString());

        eventDescriptionArea.setMaxLength(EVENT_DESCRIPTION_CHARACTERS_LIMIT);

        /* Layouts creating */
        eventDateLayout = new HorizontalLayout();
        eventDescriptionLayout = new HorizontalLayout();
        eventNameLayout = new HorizontalLayout();

        /* Enrich layouts with created components */
        eventDateLayout.addAndExpand(eventDatePicker);
        eventDescriptionLayout.addAndExpand(eventDescriptionArea);
        eventNameLayout.addAndExpand(eventNameArea);

        /* Button for confirming new event add operation */
        addEventButton = new Button("Add event");

        /* Add all layouts */
        add(eventDateLayout, eventNameLayout, eventDescriptionLayout, addEventButton);

        /* Listener for the Button object which is to add the event on click */
        addEventButton.addClickListener(e -> {
           addEventHandler();
        });
    }

}
