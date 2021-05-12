package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
    private Select<String> taskHeuristicSelect;

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
    private HorizontalLayout taskHeuristicLayout;

    private void handleSqlException(Exception e) {
        Notification.show("SQLException occurred: " + e.getMessage());
        Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
    }

    private static void handleTaskSchedulingFailure(String taskName) {
        Notification.show("Task occurrence for " + taskName  + " can not be created with this heuristic!");
    }

    private static void handleTaskSchedulingSuccess(List<CalendarEvent> events) {
        for (CalendarEvent event : events) {
            CalendarEventRepository.save(event);
            Notification.show("Task occurrence for " + event.getEventName() + " was created!");
        }
    }

    private LocalDate increaseByUnitType(char timeUnitType, LocalDate day, int timeUnits) {
        if(timeUnitType == 'D') {
            day = day.plusDays(timeUnits);
        }
        else if(timeUnitType == 'W') {
            day = day.plusWeeks(timeUnits);
        }
        else if(timeUnitType == 'M') {
            day = day.plusMonths(timeUnits);
        }
        else {
            day = day.plusYears(timeUnits);
        }
        return day;
    }

    private void addTaskHandler(char timeUnitType) {
        LocalDate startDay = LocalDate.now();
        LocalDate endDay = taskDatePicker.getValue();
        SchedulingHeuristic heuristic = SchedulingHeuristicManager.getHeuristicByName(taskHeuristicSelect.getValue());

        int timeUnits = TimeIntervalStringHandler.getTimeUnitsNumber(taskRepBreakField.getValue());
        int numRepetitions = Integer.parseInt(taskRepNumField.getValue());
        int duration = Integer.parseInt(taskDuration.getValue());
        int maxLength = Integer.parseInt(maximalContinuousLength.getValue());
        int minLength = Integer.parseInt(minimalContinuousLength.getValue());

        IntStream.range(0, numRepetitions)
                .mapToObj(i -> new SchedulingDetails(
                        increaseByUnitType(timeUnitType, startDay, i * timeUnits),
                        increaseByUnitType(timeUnitType, endDay, i * timeUnits),
                        duration,
                        maxLength,
                        minLength,
                        taskNameArea.getValue(),
                        taskDescriptionArea.getValue(),
                        tagsField.getValue()))
                .map(heuristic)
                .forEach(v -> {
                    if (v.isEmpty()) handleTaskSchedulingFailure(taskNameArea.getValue());
                    else handleTaskSchedulingSuccess(v);
                });
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
        taskDatePicker.setLabel("Choose task deadline date - not inclusive (required)");
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

        /* Text area for task tags */
        tagsField = new TextArea("Task tags (optional). Should be separated by ','. Maximum length: " +
                TASK_TAGS_CHARACTERS_LIMIT.toString());

        tagsField.setMaxLength(TASK_TAGS_CHARACTERS_LIMIT);

        taskDuration = new TextArea("Task duration (in hours, required)");
        taskDuration.setMaxLength(TASK_DURATION_CHARACTERS_LIMIT);
        taskDuration.setRequired(true);
        taskDuration.setValue(Integer.toString(2));

        taskHeuristicSelect = new Select<>();
        taskHeuristicSelect.setItems(SchedulingHeuristicManager.getAvailableHeuristicNames());
        taskHeuristicSelect.setLabel("Task scheduling heuristic");
        taskHeuristicSelect.setValue(SchedulingHeuristicManager.getAvailableHeuristicNames().stream().findFirst().orElse(""));

        minimalContinuousLength = new TextArea("Task minimal (wanted) continuous length in hours. (required). Maximum length: " + TASK_DESCRIPTION_CHARACTERS_LIMIT);
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

        taskRepBreakField = new TextArea("Time between task repetitions - integer and D (day), W (week), " +
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
        taskHeuristicLayout = new HorizontalLayout();

        taskDateTimeDivLayout.add(taskDateTimeDiv);
        taskDateTimeLayout.addAndExpand(taskDatePicker, taskDuration, minimalContinuousLength, maximalContinuousLength);
        taskNameDescDivLayout.add(taskNameDescDiv);
        taskNameDescLayout.addAndExpand(taskNameArea, taskDescriptionArea);
        taskRepDivLayout.add(taskRepDiv);
        taskRepLayout.addAndExpand(taskRepNumField, taskRepBreakField);
        tagsFieldLayout.addAndExpand(tagsField);
        taskHeuristicLayout.addAndExpand(taskHeuristicSelect);
    }

    private void insertViewComponents() {
        add(taskDateTimeDivLayout, taskDateTimeLayout);
        add(taskNameDescDivLayout, taskNameDescLayout);
        add(taskRepDivLayout, taskRepLayout);
        add(tagsFieldLayout);
        add(taskHeuristicLayout);
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
