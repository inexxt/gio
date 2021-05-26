package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.CalendarNote;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.gio.calendar.persistance.CalendarNoteRepository;
import com.gio.calendar.scheduling.SchedulingDetails;
import com.gio.calendar.scheduling.SchedulingHeuristic;
import com.gio.calendar.scheduling.SchedulingHeuristicManager;
import com.gio.calendar.utilities.TimeIntervalStringHandler;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Route(value = "new_note", layout = MainView.class)
@PageTitle("New note")
@CssImport("./views/overview/overview-view.css")
public class NewNoteView extends Div{
    /**
     * Maximum number of characters that note's name can contain
     */
    private static final Integer NOTE_NAME_CHARACTERS_LIMIT = 180;

    /**
     * Maximum number of characters that note's description can contain
     */
    private static final Integer NOTE_DESCRIPTION_CHARACTERS_LIMIT = 750;

    /**
     * Maximum number of characters that note's tags can contain
     */
    private static final Integer NOTE_TAGS_CHARACTERS_LIMIT = 180;


    private Button addNoteButton;

    private DatePicker noteDatePicker;

    private TextArea noteNameArea;
    private TextArea noteDescriptionArea;
    private TextArea tagsField;

    private Div noteDateTimeDiv;
    private Div noteNameDescDiv;

    private HorizontalLayout noteDateTimeDivLayout;
    private HorizontalLayout noteDateTimeLayout;
    private HorizontalLayout noteNameDescDivLayout;
    private HorizontalLayout noteNameDescLayout;
    private HorizontalLayout tagsFieldLayout;

    private void handleSqlException(Exception e) {
        Notification.show("SQLException occurred: " + e.getMessage());
        Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
    }

    private CalendarNote getNoteFromForm() {
        return new CalendarNote(
                noteNameArea.getValue(),
                noteDescriptionArea.getValue(),
                noteDatePicker.getValue(),
                tagsField.getValue());
    }

    private void addNoteHandler() {
        try {
            CalendarNote note = getNoteFromForm();
            CalendarNoteRepository.save(note);
        }
        catch(Exception e) {
            handleSqlException(e);
        }
    }

    private void clearForm() {
        noteNameArea.clear();
        noteDescriptionArea.clear();
        noteDatePicker.clear();
        tagsField.clear();
    }

    private void setupAddNoteButtonListener() {
        /* Listener for the Button object which is to add the task on click after
         *  checking correctness of task input data
         */
        addNoteButton.addClickListener(e -> {
            /*  Check if no task date has been provided and issue an error message in such case
             */
            try {
                if(noteDatePicker.getValue() == null) {
                    Notification.show("Error: task date has not been provided.");
                }
            else {
                    addNoteHandler();
                    clearForm();
                }
            }
            catch(NumberFormatException ex) {
                Notification.show("Error: bad format of integer strings");
            }
        });
    }


    private void initialiseAddNoteButton() {
        addNoteButton = new Button("Add note");
    }

    private void initialiseNoteDatePicker() {
        /* Picker of the new note date */
        noteDatePicker = new DatePicker();
        noteDatePicker.setLabel("Choose note date");
        noteDatePicker.setRequired(true);
        noteDatePicker.setValue(LocalDate.now());
    }

    private void initialiseTextAreas() {
        /* Task area for new note name */
        noteNameArea = new TextArea("Task name (optional). Maximum length: " +
                NOTE_NAME_CHARACTERS_LIMIT.toString());

        noteNameArea.setMaxLength(NOTE_NAME_CHARACTERS_LIMIT);
        /* Text area for new note description */
        noteDescriptionArea = new TextArea("Note description (optional). Maximum length: " +
                NOTE_DESCRIPTION_CHARACTERS_LIMIT.toString());

        noteDescriptionArea.setMaxLength(NOTE_DESCRIPTION_CHARACTERS_LIMIT);

        /* Text area for note tags */
        tagsField = new TextArea("Note tags (optional). Should be separated by ','. Maximum length: " +
                NOTE_TAGS_CHARACTERS_LIMIT.toString());

        tagsField.setMaxLength(NOTE_TAGS_CHARACTERS_LIMIT);
    }

    private void initialiseDivs() {
        noteDateTimeDiv = new Div();
        noteNameDescDiv = new Div();

        noteDateTimeDiv.getElement().setProperty("innerHTML", "<p><b>Note date");
        noteNameDescDiv.getElement().setProperty("innerHTML", "<p><b>Note name and description</b></p>");
    }

    private void initialiseLayouts() {
        noteDateTimeDivLayout = new HorizontalLayout();
        noteDateTimeLayout = new HorizontalLayout();
        noteNameDescDivLayout = new HorizontalLayout();
        noteNameDescLayout = new HorizontalLayout();
        tagsFieldLayout = new HorizontalLayout();

        noteDateTimeDivLayout.add(noteDateTimeDiv);
        noteDateTimeLayout.addAndExpand(noteDatePicker);
        noteNameDescDivLayout.add(noteNameDescDiv);
        noteNameDescLayout.addAndExpand(noteNameArea, noteDescriptionArea);
        tagsFieldLayout.addAndExpand(tagsField);
    }

    private void insertViewComponents() {
        add(noteDateTimeDivLayout, noteDateTimeLayout);
        add(noteNameDescDivLayout, noteNameDescLayout);
        add(tagsFieldLayout);
        add(addNoteButton);
    }

    public NewNoteView() {
        addClassName("newnote-view");

        initialiseAddNoteButton();
        initialiseNoteDatePicker();
        initialiseTextAreas();
        initialiseDivs();
        initialiseLayouts();
        insertViewComponents();
        setupAddNoteButtonListener();
    }
}
