package com.gio.calendar.views;

import com.gio.calendar.models.CalendarNote;
import com.gio.calendar.models.Tag;
import com.gio.calendar.persistance.CalendarNoteRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

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
            Notification.show("Note successfully added!");
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

    private void handleError(String e) {
        Notification.show("Error occurred: " + e);
    }

    private void updateNoteHandler(String noteIdString) {
        Optional<String> err = Optional.empty();
        try {
            CalendarNoteRepository.update(noteIdString, getNoteFromForm());
        }
        catch(Exception e) {
            handleSqlException(e);
        }
        finally {
            err.ifPresent(this::handleError);
        }
    }

    private void setupAddNoteButtonListener(String noteIdString) {
        /* Listener for the Button object which is to add the note on click after
         *  checking correctness of note input data
         */
        addNoteButton.addClickListener(e -> {
            /*  Check if no note date has been provided and issue an error message in such case
             */
            try {
                if(noteDatePicker.getValue() == null) {
                    Notification.show("Error: note date has not been provided.");
                }
                else {
                    if (noteIdString == null)
                        addNoteHandler();
                    else
                        updateNoteHandler(noteIdString);
                    clearForm();
                }
            }
            catch(NumberFormatException ex) {
                Notification.show("Error: bad format of integer strings");
            }
        });
    }


    private void initialiseAddNoteButton(String noteIdString) {
        addNoteButton = noteIdString == null ? new Button("Add note") : new Button("Modify note");
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
        noteNameArea = new TextArea("Note name (optional). Maximum length: " +
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

    private void setValuesIfNecessary(String noteIdString) {
        if (noteIdString != null) {
            Optional<CalendarNote> note = Optional.empty();
            try {
                note = CalendarNoteRepository.findById(Integer.parseInt(noteIdString));
            }
            catch(IllegalArgumentException e) {
                handleSqlException(e);
            }
            if(note.isPresent()) {
                noteNameArea.setValue(note.get().getNoteName());
                noteDescriptionArea.setValue(note.get().getNoteDescription());
                noteDatePicker.setValue(note.get().getNoteDate());
                tagsField.setValue(Tag.tagsToString(note.get().getNoteTags()));
            }
            else {
                Notification.show("Note with id " + noteIdString + " not found.");
            }
        }
    }

    public NewNoteView() {
        addClassName("newnote-view");
        String noteIdString = VaadinService.getCurrentRequest().getParameter("note_id");

        initialiseAddNoteButton(noteIdString);
        initialiseNoteDatePicker();
        initialiseTextAreas();
        initialiseDivs();
        initialiseLayouts();
        insertViewComponents();
        setValuesIfNecessary(noteIdString);
        setupAddNoteButtonListener(noteIdString);
    }
}
