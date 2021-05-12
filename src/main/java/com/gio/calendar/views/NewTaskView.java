package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
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
import com.vaadin.flow.server.VaadinService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Route(value = "new_task", layout = MainView.class)
@PageTitle("New task")
@CssImport("./views/newtask/newtask-view.css")
public class NewTaskView extends Div {
    /**
     * Maximum number of characters that task's name can contain
     */
    private static final Integer TASK_NAME_CHARACTERS_LIMIT = 180;

    /**
     * Maximum number of characters that task's description can contain
     */
    private static final Integer TASK_DESCRIPTION_CHARACTERS_LIMIT = 750;

    /**
     * Maximum number of characters that task's tags can contain
     */
    private static final Integer TASK_TAGS_CHARACTERS_LIMIT = 180;

    private static final Integer TASK_DURATION_CHARACTERS_LIMIT = 100;

    private Button addTaskButton;

    private DatePicker taskDatePicker;

    private TextArea taskNameArea;
    private TextArea taskDescriptionArea;
    private TextArea tagsField;
    private TextArea taskDuration;
    private TextArea minimalContinuousLength;
    private TextArea maximalContinuousLength;
    private TextArea taskRepNumField;
    private TextArea taskRepBreakField;

    private Div taskDateTimeDiv;
    private Div taskNameDescDiv;
    private Div taskRepDiv;

    private HorizontalLayout taskDateTimeDivLayout;
    private HorizontalLayout taskDateTimeLayout;
    private HorizontalLayout taskNameDescDivLayout;
    private HorizontalLayout taskNameDescLayout;
    private HorizontalLayout taskRepDivLayout;
    private HorizontalLayout taskRepLayout;
    private HorizontalLayout tagsFieldLayout;

    private void handleSqlException(Exception e) {
        Notification.show("SQLException occurred. Event has not been added.");
        Notification.show("SQLException occurred: " + e.getMessage());
        Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
    }

    private List<CalendarEvent> getEvents(List<LocalDate> days, List<Integer> starts, List<Integer> ends) {
        List<CalendarEvent> events = new ArrayList<CalendarEvent>();
        for(int i = 0; i < days.size(); ++i) {
            System.out.println(days.get(i).toString());
            events.add(new CalendarEvent(
                    taskNameArea.getValue(),
                    taskDescriptionArea.getValue(),
                    days.get(i),
                    LocalTime.of(starts.get(i), 0),
                    LocalTime.of(ends.get(i), 0),
                    tagsField.getValue(),
                    "",
                    ""));
        }

        return events;
    }

    private boolean insertionHeuristic(LocalDate startDay, LocalDate endDay) {

        int duration = Integer.parseInt(taskDuration.getValue());
        List<LocalDate> days = new ArrayList<LocalDate>();
        List<Integer> starts = new ArrayList<Integer>();
        List<Integer> ends = new ArrayList<Integer>();

        for(LocalDate i = startDay; i.compareTo(endDay) < 0; i = i.plusDays(1)) {
            boolean[] blocked = new boolean[24];
            int time = 6;
	    if(i.compareTo(startDay)==0)
		time = Math.max(6, LocalTime.now().getHour());
            for(int j = 0; j < time; ++j)
                blocked[j] = true;

            List<CalendarEvent> eventsList;
            try {
                eventsList = CalendarEventRepository.findByDate(i);
            }
            catch(Exception e) {
                return false;
            }
            for(CalendarEvent event : eventsList) {
                for(int j = event.getEventStartTime().getHour(); j < event.getEventEndTime().getHour(); ++j) {
                    blocked[j] = true;
                }
            }

            for(int j = Math.min(duration, Integer.parseInt(maximalContinuousLength.getValue()));
                 j >= Math.min(duration, Integer.parseInt(minimalContinuousLength.getValue())); --j) {
                boolean ok = false;
                for(int x = 6; x < 24 - j; ++x) {
                    for(int y = x; y < x + j; ++y) {
                        if(blocked[y])
                            break;
                        ok = (y + 1 == x + j);
                    }
                    if(ok) {
                        starts.add(x);
                        ends.add(x + j);
                        duration -= j;
                        days.add(i);
                        break;
                    }
                }
                if(ok)
                    break;
            }

        }
        if(duration != 0)
            return false;
        else {
            List<CalendarEvent> events = getEvents(days, starts, ends);
            for(CalendarEvent event : events) {
                try {
                    CalendarEventRepository.save(event);
                }
                catch(IllegalArgumentException e) {
                    handleSqlException(e);
                    return false;
                }
            }
        }
        return true;
    }

    private void addTaskHandler(char timeUnitType) {
        LocalDate startDay = LocalDate.now();
        LocalDate endDay = taskDatePicker.getValue();
        int timeUnits = TimeIntervalStringHandler.getTimeUnitsNumber(taskRepBreakField.getValue());

        for(int i = 0; i < Integer.parseInt(taskRepNumField.getValue()); ++i) {
            if(insertionHeuristic(startDay, endDay)) {
                Notification.show("Occurrence no. " + i + " of task " + taskNameArea.getValue() +
                                  " was created!");
            }
            else {
                Notification.show("Occurrence no.  " + i + " of task " + taskNameArea.getValue() +
                                  " can not be created with this heuristic!");
            }

            if(i + 1 != Integer.parseInt(taskRepNumField.getValue())) {
                if(timeUnitType == 'D') {
                    startDay = startDay.plusDays(timeUnits);
                    endDay = endDay.plusDays(timeUnits);
                }
                else if(timeUnitType == 'W') {
                    startDay = startDay.plusWeeks(timeUnits);
                    endDay = endDay.plusWeeks(timeUnits);
                }
                else if(timeUnitType == 'M') {
                    startDay = startDay.plusMonths(timeUnits);
                    endDay = endDay.plusMonths(timeUnits);
                }
                else {
                    startDay = startDay.plusYears(timeUnits);
                    endDay = endDay.plusYears(timeUnits);
                }
            }
        }
    }

    private void clearForm() {
        taskNameArea.clear();
        taskDescriptionArea.clear();
        taskDatePicker.clear();
        tagsField.clear();
        taskDuration.clear();
    }

    private void setupAddTaskButtonListener() {
        /* Listener for the Button object which is to add the task on click after
         *  checking correctness of task input data
         */
        addTaskButton.addClickListener(e -> {
            /*  Check if no task date has been provided and issue an error message in such case
             */
            try {
                if(taskDatePicker.getValue() == null) {
                    Notification.show("Error: task date has not been provided.");
                }
                else if(taskDuration.getValue() == null || Integer.parseInt(taskDuration.getValue()) < 1) {
                    Notification.show("Error: Wrong duration format");
                }
                else if(minimalContinuousLength.getValue() == null || Integer.parseInt(minimalContinuousLength.getValue()) < 1) {
                    Notification.show("Error: Wrong minimal length format");
                }
                else if(maximalContinuousLength.getValue() == null || Integer.parseInt(maximalContinuousLength.getValue()) < 1) {
                    Notification.show("Error: Wrong maximal length format");
                }
                else if(taskRepNumField.getValue().equals("1") && taskRepBreakField.getValue() != null && taskRepBreakField.getValue() != "" && Integer.parseInt(taskRepBreakField.getValue()) <= 0) {
                    Notification.show("Error: Wrong repetition values");
                }
                else if(!taskRepNumField.getValue().equals("1") && !TimeIntervalStringHandler.checkTimeIntervalString(taskRepBreakField.getValue())) {
                    Notification.show("Error: Wrong repetition values");
                } else {
                    addTaskHandler(TimeIntervalStringHandler.getTimeUnitType(taskRepBreakField.getValue()));
                    clearForm();
                }
            }
            catch(NumberFormatException ex) {
                Notification.show("Error: bad format of integer strings");
            }
        });
    }

    private void initialiseAddTaskButton() {
        addTaskButton = new Button("Add task");
    }

    private void initialiseTaskDatePicker() {
        /* Picker of the new task date */
        taskDatePicker = new DatePicker();
        taskDatePicker.setLabel("Choose task deadline date (required)");
        taskDatePicker.setRequired(true);
        taskDatePicker.setValue(LocalDate.now());
    }

    private void initialiseTextAreas() {
        /* Text area for new task name */
        taskNameArea = new TextArea("Task name (optional). Maximum length: " +
                TASK_NAME_CHARACTERS_LIMIT.toString());

        taskNameArea.setMaxLength(TASK_NAME_CHARACTERS_LIMIT);

        /* Text area for new task description */
        taskDescriptionArea = new TextArea("Task description (optional). Maximum length: " +
                TASK_DESCRIPTION_CHARACTERS_LIMIT.toString());

        taskDescriptionArea.setMaxLength(TASK_DESCRIPTION_CHARACTERS_LIMIT);

        /* Tags area for */
        tagsField = new TextArea("Task tags (optional). Should be separated by ','. Maximum length: " +
                TASK_TAGS_CHARACTERS_LIMIT.toString());

        tagsField.setMaxLength(TASK_TAGS_CHARACTERS_LIMIT);

        taskDuration = new TextArea("Task duration (in hours, required)");
        taskDuration.setMaxLength(TASK_DURATION_CHARACTERS_LIMIT);
        taskDuration.setRequired(true);
        taskDuration.setValue(Integer.toString(2));

        minimalContinuousLength = new TextArea("Minimal wanted task's continuous length (in hours, required)");
        minimalContinuousLength.setMaxLength(TASK_DESCRIPTION_CHARACTERS_LIMIT);
        minimalContinuousLength.setRequired(true);
        minimalContinuousLength.setValue(Integer.toString(1));

        maximalContinuousLength = new TextArea("Maximal wanted task's continuous length (in hours, required)");
        maximalContinuousLength.setMaxLength(TASK_DESCRIPTION_CHARACTERS_LIMIT);
        maximalContinuousLength.setRequired(true);
        maximalContinuousLength.setValue(Integer.toString(2));

        taskRepNumField = new TextArea("Number of task repetitions (required). Default: 1 " +
                "(do not change if you wish that the task occurs only once)");
        taskRepNumField.setMaxLength(4);
        taskRepNumField.setValue(Integer.toString(1));

        taskRepBreakField = new TextArea("Time between event repetitions - integer and D (day), W (week), " +
                "M (month) or Y (year). Examples: 13D, 5M.");
    }

    private void initialiseDivs() {
        taskDateTimeDiv = new Div();
        taskNameDescDiv = new Div();
        taskRepDiv = new Div();

        taskDateTimeDiv.getElement().setProperty("innerHTML", "<p><b>Task date and duration");
        taskNameDescDiv.getElement().setProperty("innerHTML", "<p><b>Task name and description</b></p>");
        taskRepDiv.getElement().setProperty("innerHTML", "<p><b>Task repetitions</b></p>");
    }

    private void initialiseLayouts() {
        taskDateTimeDivLayout = new HorizontalLayout();
        taskDateTimeLayout = new HorizontalLayout();
        taskNameDescDivLayout = new HorizontalLayout();
        taskNameDescLayout = new HorizontalLayout();
        taskRepDivLayout = new HorizontalLayout();
        taskRepLayout = new HorizontalLayout();
        tagsFieldLayout = new HorizontalLayout();

        taskDateTimeDivLayout.add(taskDateTimeDiv);
        taskDateTimeLayout.addAndExpand(taskDatePicker, taskDuration, minimalContinuousLength, maximalContinuousLength);
        taskNameDescDivLayout.add(taskNameDescDiv);
        taskNameDescLayout.addAndExpand(taskNameArea, taskDescriptionArea);
        taskRepDivLayout.add(taskRepDiv);
        taskRepLayout.addAndExpand(taskRepNumField, taskRepBreakField);
        tagsFieldLayout.addAndExpand(tagsField);
    }

    private void insertViewComponents() {
        add(taskDateTimeDivLayout, taskDateTimeLayout);
        add(taskNameDescDivLayout, taskNameDescLayout);
        add(taskRepDivLayout, taskRepLayout);
        add(tagsFieldLayout);
        add(addTaskButton);
    }

    public NewTaskView() {
        addClassName("newtask-view");

        initialiseAddTaskButton();
        initialiseTaskDatePicker();
        initialiseTextAreas();
        initialiseDivs();
        initialiseLayouts();
        insertViewComponents();
        setupAddTaskButtonListener();
    }
}
