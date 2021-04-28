package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
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

    private final Button addTaskButton;

    private final DatePicker taskDatePicker;

    private final TextArea taskNameArea;
    private final TextArea taskDescriptionArea;
    private final TextArea tagsField;
    private final TextArea taskDuration;
    private final TextArea minimalContinuousLength;
    private final TextArea maximalContinuousLength;
    private final TextArea taskRepNumField;
    private final TextArea taskRepBreakField;

    private final HorizontalLayout taskDateLayout;
    private final HorizontalLayout taskDescriptionLayout;
    private final HorizontalLayout taskNameLayout;
    private final HorizontalLayout tagsFieldLayout;
    private final HorizontalLayout taskDurationLayout;
    private final HorizontalLayout minimalLengthLayout;
    private final HorizontalLayout maximalLengthLayout;
    private final HorizontalLayout taskRepetitionNumberLayout;
    private final HorizontalLayout taskRepetitionBreakLayout;

    private void handleSqlException(Exception e) {
        Notification.show("SQLException occurred. Event has not been added.");
        Notification.show("SQLException occurred: " + e.getMessage());
        Notification.show("SQLException occurred: " + Arrays.toString(e.getStackTrace()));
    }

    private List<CalendarEvent> getEvents(List<LocalDate> days, List<Integer> starts, List<Integer> ends) {
        List<CalendarEvent> events = new ArrayList<CalendarEvent>();
        for (int i = 0; i < days.size(); ++i) {
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

    private boolean heuristic(LocalDate startDay, LocalDate endDay) {

        int duration = Integer.parseInt(taskDuration.getValue());
        List<LocalDate> days = new ArrayList<LocalDate>();
        List<Integer> starts = new ArrayList<Integer>();
        List<Integer> ends = new ArrayList<Integer>();

        for (LocalDate i = startDay; i.compareTo(endDay) < 0; i = i.plusDays(1)) {
            boolean[] blocked = new boolean[24];
            for (int j = 0; j < Math.max(6, LocalTime.now().getHour()); ++j)
                blocked[j] = true;

            List<CalendarEvent> eventsList;
            try {
                eventsList = CalendarEventRepository.findByDate(i);
            } catch (Exception e) {
                return false;
            }
            for (CalendarEvent event : eventsList) {
                for (int j = event.getEventStartTime().getHour(); j < event.getEventEndTime().getHour(); ++j) {
                    blocked[j] = true;
                }
            }

            for (int j = Math.min(duration, Integer.parseInt(maximalContinuousLength.getValue()));
                 j >= Math.min(duration, Integer.parseInt(minimalContinuousLength.getValue())); --j) {
                boolean ok = false;
                for (int x = 6; x < 24 - j; ++x) {
                    for (int y = x; y < x + j; ++y) {
                        if (blocked[y])
                            break;
                        ok = (y + 1 == x + j);
                    }
                    if (ok) {
                        starts.add(x);
                        ends.add(x + j);
                        duration -= j;
                        days.add(i);
                        break;
                    }
                }
                if (ok)
                    break;
            }

        }
        if (duration != 0)
            return false;
        else {
            List<CalendarEvent> events = getEvents(days, starts, ends);
            for (CalendarEvent event : events) {
                try {
                    CalendarEventRepository.save(event);
                } catch (IllegalArgumentException e) {
                    handleSqlException(e);
                    return false;
                }
            }
        }
        return true;
    }

    private void addTaskHandler() {
        LocalDate startDay = LocalDate.now();
        LocalDate endDay = taskDatePicker.getValue();
        for (int i = 0; i < Integer.parseInt(taskRepNumField.getValue()); ++i) {
            if (heuristic(startDay, endDay)) {
                Notification.show("Task " + i + "occurrence " + taskNameArea.getValue() + " was created!");
            } else {
                Notification.show("Task " + i + "occurrence " + taskNameArea.getValue() + " can not be created with this heuristic!");
            }
            if (i + 1 != Integer.parseInt(taskRepNumField.getValue())) {
                startDay = startDay.plusDays(Integer.parseInt(taskRepBreakField.getValue()));
                endDay = endDay.plusDays(Integer.parseInt(taskRepBreakField.getValue()));
            }
        }
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

        /* Add all layouts */
        add(taskDateLayout, taskNameLayout, taskDescriptionLayout, tagsFieldLayout,
                taskRepetitionNumberLayout, taskRepetitionBreakLayout,
                taskDurationLayout, minimalLengthLayout, maximalLengthLayout, addTaskButton);

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
