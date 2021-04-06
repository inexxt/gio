package com.gio.calendar.utilities.calendar.calendartask;

import com.gio.calendar.utilities.calendar.tag.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarTask {
    private LocalDate taskDate;

    private List<Tag> taskTag;
    private String taskDescription;
    private String taskName;
    private int duration;

    public CalendarTask(String taskName, String taskDescription, LocalDate taskDate,  String tags, int duration) {
        this.taskDate = taskDate;
        this.taskTag = Arrays.stream(tags.split(",")).map(Tag::new).collect(Collectors.toList());;
        this.taskDescription = taskDescription;
        this.taskName = taskName;
        this.duration = duration;
    }

    public CalendarTask(String taskName, String taskDescription, LocalDate taskDate,  ResultSet tags, int duration) throws SQLException {
        this.taskDate = taskDate;
        this.taskTag = new ArrayList<Tag>();
        while(tags.next()) {
            taskTag.add(new Tag(tags.getString("tag")));
        }
        this.taskDescription = taskDescription;
        this.taskName = taskName;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Date: " + taskDate.toString() +
                "\n" +
                (taskName != null ? "Name: " + taskName + "\n" : "") +
                (taskDescription != null ? "Description: " + taskDescription + "\n" : "") +
                "Tags: " + taskTag;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskTags() {
        if (taskTag.isEmpty())
            return "None";
        else {
            StringBuilder sb = new StringBuilder();
            int where = 0;
            for (Tag tag : taskTag) {
                sb.append(tag);
                ++where;
                if (taskTag.size() != where)
                    sb.append(",");
            }
            return sb.toString();
        }
    }

    public int getDuration() {
        return duration;
    }

}
