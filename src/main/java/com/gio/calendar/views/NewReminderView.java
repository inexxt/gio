package com.gio.calendar.views;

import com.gio.calendar.models.Reminder;
import com.gio.calendar.persistance.ReminderRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.dependency.CssImport;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

@Route(value = "new_reminder", layout = MainView.class)
@PageTitle("New reminder")
@CssImport("./views/newreminder/newreminder-view.css")
public class NewReminderView extends Div {

    private HorizontalLayout reminderDateDivLayout;
    private HorizontalLayout reminderDateLayout;

    private Div reminderDiv;

    private DatePicker reminderDatePicker;
    private TimePicker reminderTimePicker;
    private TextArea areaForReminderContent;

    private Button addButton;

    private void initialiseDetails() {
        reminderDatePicker = new DatePicker();
        reminderDatePicker.setLabel("Date of reminder");

        reminderTimePicker = new TimePicker();
        reminderTimePicker.setLabel("Time for reminder");

        areaForReminderContent = new TextArea();
        areaForReminderContent.setLabel("Reminder text. If you don't type in any, default will be provided");

        reminderDatePicker.setValue(LocalDate.now());
        reminderTimePicker.setValue(LocalTime.of(12, 0));

        addButton = new Button("Add reminder");
    }

    private void initialiseLayouts() {
        reminderDateDivLayout = new HorizontalLayout();
        reminderDateLayout = new HorizontalLayout();

        reminderDateDivLayout.add(reminderDiv);
        reminderDateLayout.add(reminderDatePicker, reminderTimePicker, areaForReminderContent);
    }

    private void initialiseDivs() {
        reminderDiv = new Div();

        reminderDiv.getElement().setProperty("innerHTML", "<p><b>Reminder details</b></p>");
    }

    private void addComponents() {
        add(reminderDateDivLayout);
        add(reminderDateLayout);

        add(addButton);
    }

    private Reminder getReminderFromForm() {
        return new Reminder(reminderDatePicker.getValue(),
                            reminderTimePicker.getValue(),
                            areaForReminderContent.getValue() == null ||
                            areaForReminderContent.getValue().equals("") ? "Default reminder text" :
                            areaForReminderContent.getValue());
    }

    private void clearForms() {
        reminderDatePicker.setValue(LocalDate.now());
        reminderTimePicker.setValue(LocalTime.of(12, 0));
        areaForReminderContent.setValue("");
    }

    private void addCreateButtonListener() {
        addButton.addClickListener(event -> {
            if(reminderDatePicker.getValue() == null) {
                Notification.show("No date for reminder has been provided");
            }
            else if(reminderTimePicker.getValue() == null) {
                Notification.show("No time for reminder has been provided");
            }
            else {
                Reminder created = getReminderFromForm();
                ReminderRepository.save(created);

                Notification.show("Reminder successfully created");

                clearForms();
            }
        });
    }

    public NewReminderView() {
        addClassName("newreminder-view");

        initialiseDivs();
        initialiseDetails();

        initialiseLayouts();
        addComponents();

        addCreateButtonListener();
    }
}

