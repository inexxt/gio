package com.gio.calendar.utilities.calendar.calendartask;

import com.gio.calendar.utilities.calendar.tag.Tag;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarTask {
    private LocalDate taskDate;

    private List<Tag> taskTag;
    private String taskDescription;
    private String taskName;

    public CalendarTask(String taskName, String taskDescription, LocalDate taskDate,  String tags) {
        this.taskDate = taskDate;
        this.taskTag = Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toList());;
        this.taskDescription = taskDescription;
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "Date: " + taskDate.toString() +
                "\n" +
                (taskName != null ? "Name: " + taskName + "\n" : "") +
                (taskDescription != null ? "Description: " + taskDescription + "\n" : "") +
                "Tags: ";
    }

}
