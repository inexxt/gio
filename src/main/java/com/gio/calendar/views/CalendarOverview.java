package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.models.CalendarNote;
import com.gio.calendar.models.Person;
import com.gio.calendar.models.Tag;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.gio.calendar.utilities.TimeDateUtils;
import com.gio.calendar.persistance.CalendarNoteRepository;
import com.gio.calendar.utilities.TimeZoneUtils;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.dialog.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.server.VaadinService;
import org.apache.tomcat.jni.Local;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


@Route(value = "overview", layout = MainView.class)
@PageTitle("Calendar overview")
@CssImport("./views/overview/overview-view.css")
public class CalendarOverview extends Div {
    private static LocalDate dateToSet = null;
    private static boolean forceDatePickerValue = false;
    private static boolean displayCurrentDayEventsNotifications = true;

    private static final int OPT_DAILY = 0;
    private static final int OPT_WEEKLY = 1;
    private static final int OPT_MONTHLY = 2;

    private static int overviewType = OPT_DAILY; /* Default - daily overview */

    private DatePicker targetDatePicker;

    private TextArea tagsField;

    private Set<Tag> tagsForFiltering = null;

    private static final List<String> dayLabels = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    private Select<String> overviewOptionsSelect;

    /**
     * Sets notes info in layouts.
     * @param notesIndex - index of first layout in array that can be filled with
     * appropriate note info
     * @param infoLayouts - array of layouts that are to be filled
     * @param notesList - list of calendar notes whose info shall be
     * present in layouts
     */
    private void setNotesInfo(int notesIndex, VerticalLayout[] infoLayouts, List<CalendarNote> notesList) {
        int notesNo = 1;

        if(notesList.size() == 0) {
            infoLayouts[notesIndex].add(new Label("No notes for specified day"));
        }

        for (CalendarNote e : notesList) {
            /* Note tag displaying information in format "Note + n" where n is
             * ordinal number of note in specified day (order: 1, 2, ...)
             */
            Label noteTag = new Label("Note " + notesNo);
            noteTag.setWidth(null);
            noteTag.setHeight("10px");
            noteTag.getStyle().set("font-weight", "bold");

            infoLayouts[notesIndex].add(noteTag);

            /* Array of break labels to be input between note data fields */
            Label[] breakLabels = new Label[3];

            /* Array of text labels to display specified note data
             */
            Label[] textLabels = new Label[3];

            /* Set up break labels
             */
            for (int i = 0; i < 3; ++i) {
                breakLabels[i] = new Label("");
                breakLabels[i].setWidth(null);
                breakLabels[i].setHeight("0.1px");
            }

            /* Set up text labels
             */
            textLabels[0] = new Label("Name: " + e.getNoteName());
            textLabels[1] = new Label("Description: " + e.getNoteDescription());
            textLabels[2] = new Label("Tags: " + Tag.tagsToString(e.getNoteTags()));
            /* Set width and height of text labels and add both break and text labels
             * to the display
             */
            for (int i = 0; i < 3; ++i) {
                textLabels[i].setWidth("30%");
                textLabels[i].setHeight("10px");

                infoLayouts[notesIndex].add(textLabels[i]);
                infoLayouts[notesIndex].add(breakLabels[i]);
            }

            /* Layout for displaying buttons which are to handle available actions
             * concerning the specified note:
             * Note data modification
             * Note deletion
             */
            HorizontalLayout noteActionsLayout = new HorizontalLayout();

            Button noteModificationButton = new Button("Modify note data");
            Button noteDeleteButton = new Button("Delete note");

            noteActionsLayout.add(noteModificationButton, noteDeleteButton);
            infoLayouts[notesIndex].add(noteActionsLayout);

            noteModificationButton.addClickListener(v -> {
                UI.getCurrent().getPage().setLocation("new_note" + "?" + "note_id=" + e.getNoteId());
            });

            addNoteDeleteConfirmation(noteDeleteButton, e);
            notesIndex++;
            notesNo++;
        }
    }

    /**
     * Sets events info in layouts.
     * @param infoLayouts - array of layouts that are to be filled
     * @param eventsList - list of calendar events whose info shall be
     * present in layouts
     * @return integer - first index of layout from the passed array that can be further used as
     * layout for storing calendar note data
     */
    private int setEventsInfo(VerticalLayout[] infoLayouts, List<CalendarEvent> eventsList) {
        int eventIndex = 0;
        int eventNo = 1;

        if(eventsList.size() == 0) {
            infoLayouts[eventIndex].add(new Label("No events scheduled for specified day"));
            eventIndex++;

            return eventIndex;
        }

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

            textLabels[0] = new Label("Name: " + e.getEventName());
            textLabels[1] = new Label("Description: " + e.getEventDescription());
            textLabels[2] = new Label("Start time: " + e.getEventStartTimeString() +
                    " (your current timezone: " +
                    TimeDateUtils.zonedTimeToString(TimeZoneUtils.atSystemTimezone(e.getEventStartTime(), e.getEventDate())) + ")");
            textLabels[3] = new Label("End time: " + e.getEventEndTimeString() +
                    " (your current timezone: " +
                    TimeDateUtils.zonedTimeToString(TimeZoneUtils.atSystemTimezone(e.getEventEndTime(), e.getEventDate())) + ")");
            textLabels[4] = new Label("Tags: " + Tag.tagsToString(e.getEventTags()));
            textLabels[5] = new Label("Place: " + e.getEventPlace());
            textLabels[6] = new Label("Guests: " + Person.peopleToString(e.getEventPeople()));
            /* Set width and height of text labels and add both break and text labels
             * to the display
             */
            for (int i = 0; i < 7; ++i) {
                textLabels[i].setWidth("60%");
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

            addEventDeleteConfirmation(eventDeleteButton, e);
            eventIndex++;
            eventNo++;
        }

        return eventIndex;
    }

    /**
     * Adds click listener to button which is to handle note deletion
     * @param noteDeleteButton - button to which listener shall be added
     * @param e - which note should be affected by the listener
     */
    private void addNoteDeleteConfirmation(Button noteDeleteButton, CalendarNote e) {
        noteDeleteButton.addClickListener(v -> {
            Dialog deleteDialog = new Dialog();

            /* Layouts with following purposes:
             * queryLayout - stores Label with query text
             * buttonsLayout - stores Buttons which execute further actions
             */
            HorizontalLayout queryLayout = new HorizontalLayout();
            HorizontalLayout buttonsLayout = new HorizontalLayout();

            Label deleteConfirmationQuery = new Label("Please confirm operation");
            Button noteDeleteConfirmation = new Button("Confirm note deletion");
            Button noteDeleteCancellation = new Button("Cancel note deletion");

            queryLayout.add(deleteConfirmationQuery);
            buttonsLayout.add(noteDeleteCancellation, noteDeleteConfirmation);

            deleteDialog.add(queryLayout, buttonsLayout);

            deleteDialog.setVisible(true);
            deleteDialog.open();

            noteDeleteCancellation.addClickListener(w -> {
                deleteDialog.close();
            });

            noteDeleteConfirmation.addClickListener(w -> {
                /* Flag to indicate whether note deletion was successful
                 */
                boolean okDeletion = true;
                /* Store current date stored in targetDatePicker to set it as
                 * targetDatePicker value after the page refresh which occurs
                 * after successful note deletion
                 */
                LocalDate saveDate = targetDatePicker.getValue();

                try {
                    CalendarNoteRepository.deleteById(e.getNoteId());
                } catch (IllegalArgumentException ex) {
                    /* Mark deletion as unsuccessful and issue proper notification informing
                     * about the exception that has occurred
                     */
                    okDeletion = false;
                    Notification.show("Exception occured. Note has not been deleted.");
                } finally {
                    /* Proceed with page update on successful deletion of event data
                     * from the database
                     */
                    if (okDeletion) {
                        Notification.show("Note has been successfully deleted.");
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

    /**
     * Adds click listener to button which is to handle event deletion
     * @param eventDeleteButton - button to which listener shall be added
     * @param e - which event should be affected by the listener
     */
    private void addEventDeleteConfirmation(Button eventDeleteButton, CalendarEvent e) {
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

    /**
     * Gets the date which should be viewed (executed on view loading)
     * @return LocalDate object representing the date which should be viewed
     */
    private LocalDate getDateToView() {
        if (forceDatePickerValue) {
            forceDatePickerValue = false;
            return dateToSet;
        }

        String passedDateString = VaadinService.getCurrentRequest().getParameter("date");
        LocalDate dateToReturn = LocalDate.now();

        if(passedDateString != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                dateToReturn = LocalDate.parse(passedDateString, dateFormatter);
            }
            catch(DateTimeParseException e) {
                /* suppress */
            }
        }

        return dateToReturn;
    }

    /**
     * Displays notification about each event scheduled for
     * current date
     */
    private void notifyAboutCurrentDayEvents() {
        List<CalendarEvent> todayEvents = CalendarEventRepository.findByDate(LocalDate.now());

        if(todayEvents.size() == 0) {
            Notification.show("No events scheduled for today");
        }
        else {
            Notification.show("Events scheduled for today:");
            for(CalendarEvent e: todayEvents) {
                Notification.show("Event " + e.getEventName() + " on " + e.getEventStartTimeString());
            }
        }
    }

    /**
     * Initialises date picker for daily overview
     */
    private void initialiseDatePickerDaily() {
        targetDatePicker.addValueChangeListener(e -> {
            if(targetDatePicker.getValue() != null) {
                renderDailyOverview(targetDatePicker.getValue());
            }
        });
    }

    /**
     * Initialises date picker for weekly overview
     */
    private void initialiseDatePickerWeekly() {
        targetDatePicker.addValueChangeListener(e -> {
            if(targetDatePicker.getValue() != null) {
                renderWeeklyOverview(targetDatePicker.getValue());
            }
        });
    }

    /**
     * Initialises date picker for monthly overview
     */
    private void initialiseDatePickerMonthly() {
        targetDatePicker.addValueChangeListener(e -> {
            if(targetDatePicker.getValue() != null) {
                renderMonthlyOverview(targetDatePicker.getValue());
            }
        });
    }

    /**
     * Gets collection of calendar events that are scheduled for passed date,
     * filtered according to possessed collection of tags according to which notes
     * should be filtered
     * @param date - on which date calendar events should be filtered
     * @return list of filtered calendar events
     */
    private List<CalendarEvent> getFilteredEventsOnDate(LocalDate date) {
        List<CalendarEvent> eventsOnDate = CalendarEventRepository.findByDate(date);

        if(tagsForFiltering == null || tagsForFiltering.size() == 0) {
            return eventsOnDate;
        }

        Iterator<CalendarEvent> iterator = eventsOnDate.iterator();

        while(iterator.hasNext()) {
            CalendarEvent e = iterator.next();
            boolean containsAtLeastOneTag = false;

            for(Tag t: e.getEventTags()) {
                if(tagsForFiltering.contains(t)) {
                    containsAtLeastOneTag = true;
                    break;
                }
            }

            if(!containsAtLeastOneTag) {
                iterator.remove();
            }
        }

        return eventsOnDate;
    }

    /**
     * Gets collection of notes that are scheduled for passed date, filtered
     * according to possessed collection of tags according to which notes
     * should be filtered
     * @param date - on which date notes should be filtered
     * @return list of filtered notes
     */
    private List<CalendarNote> getFilteredNotesOnDate(LocalDate date) {
        List<CalendarNote> notesOnDate = CalendarNoteRepository.findByDate(date);

        if(tagsForFiltering == null || tagsForFiltering.size() == 0) {
            return notesOnDate;
        }

        Iterator<CalendarNote> iterator = notesOnDate.iterator();

        while(iterator.hasNext()) {
            CalendarNote n = iterator.next();
            boolean containsAtLeastOneTag = false;

            for(Tag t: n.getNoteTags()) {
                if(tagsForFiltering.contains(t)) {
                    containsAtLeastOneTag = true;
                    break;
                }
            }

            if(!containsAtLeastOneTag) {
                iterator.remove();
            }
        }

        return notesOnDate;
    }

    /**
     * Renders daily overview
     * @param targetDate - the date which is to be displayed in rendered overview
     * right after rendering
     */
    private void renderDailyOverview(LocalDate targetDate) {
        removeAll();
        addBasicComponentsAfresh();
        initialiseDatePickerDaily();

        List<CalendarEvent> eventsOnDate = getFilteredEventsOnDate(targetDate);
        List<CalendarNote> notesOnDate = getFilteredNotesOnDate(targetDate);

        int layoutsCount = eventsOnDate.size() + notesOnDate.size();

        if(eventsOnDate.size() == 0) {
            layoutsCount++;
        }

        if(notesOnDate.size() == 0) {
            layoutsCount++;
        }

        VerticalLayout[] layoutsArray = new VerticalLayout[layoutsCount];

        /* Instantiate the vertical layouts in array */
        for(int i = 0; i < layoutsCount; ++i) {
            layoutsArray[i] = new VerticalLayout();
        }

        int firstForNotes = setEventsInfo(layoutsArray, eventsOnDate);
        setNotesInfo(firstForNotes, layoutsArray, notesOnDate);

        for(VerticalLayout v: layoutsArray) {
            add(v);
        }
    }

    /**
     * Renders weekly overview
     * @param targetDate - the date which is to be displayed in rendered overview
     * right after rendering
     */
    private void renderWeeklyOverview(LocalDate targetDate) {
        removeAll();
        addBasicComponentsAfresh();
        initialiseDatePickerWeekly();

        HorizontalLayout weekLayout = new HorizontalLayout();

        int dayNumber = targetDate.getDayOfWeek().getValue();

        for(int i = 0; i < 7; ++i) {
            VerticalLayout currentDayLayout = new VerticalLayout();

            LocalDate dayDate = targetDate.plusDays(i + 1 - dayNumber);

            Button dayButton = new Button();
            dayButton.setText(dayLabels.get(i) + ", " + dayDate);

            dayButton.addClickListener(e -> {
                targetDatePicker.setValue(dayDate);
                overviewOptionsSelect.setValue("Daily");
            });

            currentDayLayout.add(dayButton);

            List<CalendarEvent> eventsForCurrentDay = getFilteredEventsOnDate(dayDate);

            if(eventsForCurrentDay.size() != 0) {
                currentDayLayout.add(new Label("Events scheduled:"));

                for(CalendarEvent c: eventsForCurrentDay) {
                    Label forCurrentEvent = new Label(c.getEventName());
                    forCurrentEvent.setTitle(c.getEventDescription());

                    currentDayLayout.add(forCurrentEvent);
                }
            }
            else {
                currentDayLayout.add(new Label("No events"));
            }

            List<CalendarNote> notesForCurrentDay = getFilteredNotesOnDate(dayDate);

            if(notesForCurrentDay.size() != 0) {
                currentDayLayout.add(new Label("Notes:"));

                for(CalendarNote n: notesForCurrentDay) {
                    Label forCurrentNote = new Label(n.getNoteName());
                    forCurrentNote.setTitle(n.getNoteDescription());

                    currentDayLayout.add(forCurrentNote);
                }
            }
            else {
                currentDayLayout.add(new Label("No notes"));
            }

            weekLayout.add(currentDayLayout);
        }

        add(weekLayout);
    }

    /**
     * Adds day names' labels to weekly overview
     */
    private void addDayLabelsInMonthlyOverview() {
        HorizontalLayout forDaysLabels = new HorizontalLayout();

        for(int i = 0; i < 7; ++i) {
            forDaysLabels.add(new VerticalLayout(new Label(dayLabels.get(i))));
        }

        add(forDaysLabels);
    }

    /**
     * Renders monthly overview
     * @param targetDate - the date which is to be displayed in rendered overview
     * right after rendering
     */
    private void renderMonthlyOverview(LocalDate targetDate) {
        removeAll();
        addBasicComponentsAfresh();
        initialiseDatePickerMonthly();

        addDayLabelsInMonthlyOverview();

        boolean continueInserting = true;

        int currentMonthValue = targetDate.getMonthValue();

        int whichDayInMonth = targetDate.getDayOfMonth();

        LocalDate dateCopy = targetDate.minusDays(whichDayInMonth - 1);

        int whichDayInWeek = dateCopy.getDayOfWeek().getValue();

        while(continueInserting) {
            HorizontalLayout currentRow = new HorizontalLayout();

            for(int i = 0; i < 7; ++i) {
                VerticalLayout forCurrentDay = new VerticalLayout();

                LocalDate iterationDate = dateCopy.plusDays(i + 1 - whichDayInWeek);

                if(iterationDate.getMonthValue() == currentMonthValue) {
                    Button currentDayButton = new Button();

                    Integer dayNumber = iterationDate.getDayOfMonth();

                    currentDayButton.setText(dayNumber.toString());
                    currentDayButton.addClickListener(e -> {

                        targetDatePicker.setValue(iterationDate);
                        overviewOptionsSelect.setValue("Daily");
                    });


                    Label[] labelsForDay = { new Label(""), new Label("") };
                    int labelIndex = 0;

                    int numberOfEventsForCurrentDay = getFilteredEventsOnDate(iterationDate).size();
                    int numberOfNotesForCurrentDay = getFilteredNotesOnDate(iterationDate).size();

                    if(numberOfEventsForCurrentDay != 0) {
                        labelsForDay[labelIndex].setText("Events: " + numberOfEventsForCurrentDay);
                        labelIndex++;
                    }

                    if(numberOfNotesForCurrentDay != 0) {
                        labelsForDay[labelIndex].setText("Notes: " + numberOfNotesForCurrentDay);
                    }

                    forCurrentDay.add(currentDayButton, labelsForDay[0], labelsForDay[1]);
                }

                currentRow.add(forCurrentDay);
            }

            add(currentRow);

            dateCopy = dateCopy.plusWeeks(1);

            continueInserting = (dateCopy.minusDays(whichDayInWeek - 1).getMonthValue() ==
                                 targetDate.getMonthValue());
        }
    }

    /**
     * Initialises select which handles switching between
     * possible overview types
     */
    private void initialiseSelect() {
        overviewOptionsSelect = new Select<>();

        overviewOptionsSelect.setLabel("Overview type");
        overviewOptionsSelect.setItems("Daily", "Weekly", "Monthly");

        overviewOptionsSelect.addValueChangeListener(e -> {
            String selectValue = overviewOptionsSelect.getValue();
            if(selectValue != null && targetDatePicker.getValue() != null) {
                if(selectValue.compareTo("Daily") == 0) {
                    overviewType = OPT_DAILY;
                    renderDailyOverview(targetDatePicker.getValue());
                }
                else if(selectValue.compareTo("Weekly") == 0) {
                    overviewType = OPT_WEEKLY;
                    renderWeeklyOverview(targetDatePicker.getValue());
                }
                else {
                    overviewType = OPT_MONTHLY;
                    renderMonthlyOverview(targetDatePicker.getValue());
                }
            }
        });
    }

    /**
     * Re-adds basic components (common for each overview type) to the view
     */
    private void addBasicComponentsAfresh() {
        HorizontalLayout forBasicComponents = new HorizontalLayout(overviewOptionsSelect,
                                                                   targetDatePicker,
                                                                   tagsField);
        add(forBasicComponents);
    }

    /**
     * Adds value change listener to textarea responsible for
     * grabbing data about tags for filtering events
     */
    private void addFiltrationValueChangeListener() {
        tagsField.addValueChangeListener(e -> {
            if(tagsField.getValue() == null) {
                tagsForFiltering = null;
            }
            else {
                /* Initialise the set which contains the tags according to
                 * which events and notes are filtered
                 */
                tagsForFiltering = Tag.tagsFromString(tagsField.getValue());

                if(overviewType == OPT_DAILY) {
                    renderDailyOverview(targetDatePicker.getValue());
                }
                else if(overviewType == OPT_WEEKLY) {
                    renderWeeklyOverview(targetDatePicker.getValue());
                }
                else if(overviewType == OPT_MONTHLY) {
                    renderMonthlyOverview(targetDatePicker.getValue());
                }
            }
        });
    }

    /**
     * Initialises and adds basic components to the view
     */
    private void addBasicComponents() {
        HorizontalLayout forBasicComponents = new HorizontalLayout();

        initialiseSelect();

        targetDatePicker = new DatePicker();
        targetDatePicker.setLabel("Select date to view");

        tagsField = new TextArea();
        tagsField.setValue("None");
        tagsField.setLabel("Tags for filtration (separated by comma)");

        addFiltrationValueChangeListener();

        forBasicComponents.add(overviewOptionsSelect, targetDatePicker, tagsField);

        add(forBasicComponents);
    }

    /**
     * Constructor of the calendar overview class
     */
    public CalendarOverview() {
        addClassName("overview-view");

        Notification.show("Establishing connection to db and initializing data");
        Notification.show("Connection established");

        addBasicComponents();

        targetDatePicker.setValue(getDateToView());

        if(overviewType == OPT_DAILY) {
            overviewOptionsSelect.setValue("Daily");
        }
        else if(overviewType == OPT_WEEKLY) {
            overviewOptionsSelect.setValue("Weekly");
        }
        else if(overviewType == OPT_MONTHLY) {
            overviewOptionsSelect.setValue("Monthly");
        }

        if(displayCurrentDayEventsNotifications) {
            notifyAboutCurrentDayEvents();
            displayCurrentDayEventsNotifications = false;
        }
    }
}
