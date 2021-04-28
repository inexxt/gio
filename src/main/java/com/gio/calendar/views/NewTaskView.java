package com.gio.calendar.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

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

    private static final Integer TASK_DURATION_CHARACTERS_LIMIT = 2;

    private final Button addTaskButton;

    private final DatePicker taskDatePicker;

    private final TextArea taskNameArea;
    private final TextArea taskDescriptionArea;
    private final TextArea tagsField;
    private final TextArea taskDuration;

    private final HorizontalLayout taskDateLayout;
    private final HorizontalLayout taskDescriptionLayout;
    private final HorizontalLayout taskNameLayout;
    private final HorizontalLayout tagsFieldLayout;
    private final HorizontalLayout taskDurationLayout;

    private void addTaskHandler() {
        // Here will be the handler for the tasks
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

        taskDuration = new TextArea("Task duration (optional) in hours. Maximuum length: " + TASK_DURATION_CHARACTERS_LIMIT.toString());
        taskDuration.setMaxLength(TASK_DURATION_CHARACTERS_LIMIT);

        /* Layouts creating */
        taskDateLayout = new HorizontalLayout();
        taskDescriptionLayout = new HorizontalLayout();
        taskNameLayout = new HorizontalLayout();
        tagsFieldLayout = new HorizontalLayout();
        taskDurationLayout = new HorizontalLayout();

        /* Button for confirming new task add operation */
        addTaskButton = new Button("Add task");

        /* Enrich layouts with created components */
        taskDateLayout.addAndExpand(taskDatePicker);
        taskDescriptionLayout.addAndExpand(taskDescriptionArea);
        taskNameLayout.addAndExpand(taskNameArea);
        tagsFieldLayout.addAndExpand(tagsField);
        taskDurationLayout.addAndExpand(taskDuration);

        /* Add all layouts */
        add(taskDateLayout, taskNameLayout, taskDescriptionLayout, tagsFieldLayout, taskDurationLayout, addTaskButton);
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
                } else if (taskDuration.getValue() != null
                        && (Integer.parseInt(taskDuration.getValue()) < 1
                        || Integer.parseInt(taskDuration.getValue()) > 24)) {
                    Notification.show("Error: Wrong duration format");
                } else {
                    addTaskHandler();
                    Notification.show("Task " + taskNameArea.getValue() + " was created!");
                    clearForm();
                }
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
