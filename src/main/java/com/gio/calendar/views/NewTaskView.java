package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.gio.calendar.scheduling.SchedulingDetails;
import com.gio.calendar.scheduling.SchedulingHeuristic;
import com.gio.calendar.scheduling.SchedulingHeuristicManager;
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

    private final Button addTaskButton;

    private final DatePicker taskDatePicker;

    private final TextArea taskNameArea;
    private final TextArea taskDescriptionArea;
    private final TextArea tagsField;
    private final TextArea taskDuration;
    private final Select<String> taskHeuristicSelect;
    private final TextArea minimalContinuousLength;
    private final TextArea maximalContinuousLength;
    private final TextArea taskRepNumField;
    private final TextArea taskRepBreakField;

    private final HorizontalLayout taskDateLayout;
    private final HorizontalLayout taskDescriptionLayout;
    private final HorizontalLayout taskNameLayout;
    private final HorizontalLayout tagsFieldLayout;
    private final HorizontalLayout taskHeuristicLayout;
    private final HorizontalLayout taskDurationLayout;
    private final HorizontalLayout minimalLengthLayout;
    private final HorizontalLayout maximalLengthLayout;
    private final HorizontalLayout taskRepetitionNumberLayout;
    private final HorizontalLayout taskRepetitionBreakLayout;

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

    private void addTaskHandler() {
        LocalDate startDay = LocalDate.now();
        LocalDate endDay = taskDatePicker.getValue();
        SchedulingHeuristic heuristic = SchedulingHeuristicManager.getHeuristicByName(taskHeuristicSelect.getValue());

        int numRepetitions = Integer.parseInt(taskRepNumField.getValue());
        int duration = Integer.parseInt(taskDuration.getValue());
        int maxLength = Integer.parseInt(maximalContinuousLength.getValue());
        int minLength = Integer.parseInt(minimalContinuousLength.getValue());
        int taskRepBreak = Integer.parseInt(taskRepBreakField.getValue());

        IntStream.range(0, numRepetitions)
                .mapToObj(i -> new SchedulingDetails(
                        startDay.plusDays((long) i * taskRepBreak),
                        endDay.plusDays((long) i * taskRepBreak),
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

    public NewTaskView() {
        addClassName("newtask-view");

        /* Picker of the new task date */
        taskDatePicker = new DatePicker();
        taskDatePicker.setLabel("Choose task date (required)");
        taskDatePicker.setRequired(true);

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

        taskDuration = new TextArea("Task duration (required) in hours. Maximuum length: " + TASK_DURATION_CHARACTERS_LIMIT.toString());
        taskDuration.setMaxLength(TASK_DURATION_CHARACTERS_LIMIT);
        taskDuration.setRequired(true);

        taskHeuristicSelect = new Select<>();
        taskHeuristicSelect.setItems(SchedulingHeuristicManager.getAvailableHeuristicNames());
        taskHeuristicSelect.setLabel("Task scheduling heuristic");

        minimalContinuousLength = new TextArea("Task minimal (wanted) continuous length in hours. (required). Maximum length: " + TASK_DESCRIPTION_CHARACTERS_LIMIT);
        minimalContinuousLength.setMaxLength(TASK_DESCRIPTION_CHARACTERS_LIMIT);
        minimalContinuousLength.setRequired(true);

        maximalContinuousLength = new TextArea("Task maximal (wanted) continuous length in hours. (required). Maximum length: " + TASK_DESCRIPTION_CHARACTERS_LIMIT);
        maximalContinuousLength.setMaxLength(TASK_DESCRIPTION_CHARACTERS_LIMIT);
        maximalContinuousLength.setRequired(true);

        taskRepNumField = new TextArea("Number of task repetitions (required). Default: 1 " +
                "(do not change if you wish that the task occurs only once");
        taskRepNumField.setMaxLength(4);
        taskRepNumField.setValue(Integer.toString(1));

        taskRepBreakField = new TextArea("Time interval (in days) between repetitions of task, required for more than one task " +
                "repetition");


        /* Layouts creating */
        taskDateLayout = new HorizontalLayout();
        taskDescriptionLayout = new HorizontalLayout();
        taskNameLayout = new HorizontalLayout();
        tagsFieldLayout = new HorizontalLayout();
        taskDurationLayout = new HorizontalLayout();
        minimalLengthLayout = new HorizontalLayout();
        maximalLengthLayout = new HorizontalLayout();
        taskRepetitionNumberLayout = new HorizontalLayout();
        taskRepetitionBreakLayout = new HorizontalLayout();

        taskHeuristicLayout = new HorizontalLayout();

        /* Button for confirming new task add operation */
        addTaskButton = new Button("Add task");

        /* Enrich layouts with created components */
        taskDateLayout.addAndExpand(taskDatePicker);
        taskDescriptionLayout.addAndExpand(taskDescriptionArea);
        taskNameLayout.addAndExpand(taskNameArea);
        tagsFieldLayout.addAndExpand(tagsField);
        taskDurationLayout.addAndExpand(taskDuration);
        minimalLengthLayout.addAndExpand(minimalContinuousLength);
        maximalLengthLayout.addAndExpand(maximalContinuousLength);
        taskRepetitionNumberLayout.addAndExpand(taskRepNumField);
        taskRepetitionBreakLayout.addAndExpand(taskRepBreakField);

        taskHeuristicLayout.addAndExpand(taskHeuristicSelect);

        /* Add all layouts */
        add(taskDateLayout, taskNameLayout, taskDescriptionLayout, tagsFieldLayout,
                taskRepetitionNumberLayout, taskRepetitionBreakLayout,
                taskDurationLayout, minimalLengthLayout, maximalLengthLayout, taskHeuristicLayout, addTaskButton);

        setupListeners();
    }

    private void setupListeners() {
        /* Listener for the Button object which is to add the task on click after
         *  checking correctness of task input data
         */
        addTaskButton.addClickListener(e -> {
            /*  Check if no task date has been provided and issue an error message in such case
             */
            try {
                if (taskDatePicker.getValue() == null) {
                    Notification.show("Error: task date has not been provided.");
                } else if (taskDuration.getValue() == null || Integer.parseInt(taskDuration.getValue()) < 1) {
                    Notification.show("Error: Wrong duration format");
                } else if (minimalContinuousLength.getValue() == null || Integer.parseInt(minimalContinuousLength.getValue()) < 1) {
                    Notification.show("Error: Wrong minimal length format");
                } else if (maximalContinuousLength.getValue() == null || Integer.parseInt(maximalContinuousLength.getValue()) < 1) {
                    Notification.show("Error: Wrong maximal length format");
                } else if (taskRepNumField.getValue().equals("1") && taskRepBreakField.getValue() != null && taskRepBreakField.getValue() != "" && Integer.parseInt(taskRepBreakField.getValue()) <= 0) {
                    Notification.show("Error: Wrong repetition values");
                } else if (!taskRepNumField.getValue().equals("1") && Integer.parseInt(taskRepBreakField.getValue()) <= 0 || Integer.parseInt(taskRepNumField.getValue()) <= 0) {
                    Notification.show("Error: Wrong repetition values");
                } else {
                    addTaskHandler();
                    clearForm();
                }
            } catch (IllegalArgumentException exc){
                handleSqlException(exc);
            } catch (Exception exc) {
                Notification.show("Error: " + exc);
            }
        });
    }

    private void clearForm() {
        taskNameArea.clear();
        taskDescriptionArea.clear();
        taskDatePicker.clear();
        tagsField.clear();
        taskDuration.clear();
    }
}
