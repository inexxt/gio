package com.gio.calendar.utilities.database;

import com.gio.calendar.utilities.calendar.tag.Tag;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class InsertManager {

    public static void addTags(String taskOrEvent, ResultSet res, List<Tag> tags) throws SQLException, IOException, ClassNotFoundException {
        Connection conn = ConnectionManager.getConnection();
        while(res.next()) {
            for (Tag t : tags) {
                String sql = "INSERT INTO " + taskOrEvent + "_tags("+ taskOrEvent + ", tag) VALUES(?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, res.getString(1));
                pstmt.setString(2, t.toString());
                pstmt.executeUpdate();
            }
        }
    }

    public static ResultSet addTask(LocalDate taskTime, String name, String description, String duration)
            throws SQLException, IOException, ClassNotFoundException {

        LocalDateTime taskDate = taskTime.atStartOfDay();
        Connection conn = ConnectionManager.getConnection();
        String sql = "INSERT INTO tasks(name, desc, task_date, task_duration) VALUES(?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, description);

        // slight incompatiblity between timestamp in miliseconds and in seconds
        pstmt.setInt(3, (int) (Timestamp.valueOf(taskDate).getTime() / 1000L));
        pstmt.setInt(4, Integer.parseInt(duration));

        pstmt.executeUpdate();
        return pstmt.getGeneratedKeys();
    }

    public static ResultSet addEvent(LocalDate eventDate, LocalTime eventStartTime, LocalTime eventEndTime,
                                     String name, String description) throws SQLException, IOException, ClassNotFoundException {

        LocalDateTime eventStart = LocalDateTime.of(eventDate.getYear(),
                eventDate.getMonthValue(),
                eventDate.getDayOfMonth(),
                eventStartTime.getHour(),
                eventStartTime.getMinute());

        LocalDateTime eventEnd = LocalDateTime.of(eventDate.getYear(),
                eventDate.getMonthValue(),
                eventDate.getDayOfMonth(),
                eventEndTime.getHour(),
                eventEndTime.getMinute());

        Connection conn = ConnectionManager.getConnection();
        String sql = "INSERT INTO events(name, desc, event_start, event_end) VALUES(?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, (name != null ?
                name : "Event name not provided."));

        pstmt.setString(2, (description != null ?
                description : "Event description not provided."));

        pstmt.setInt(3, (int) (Timestamp.valueOf(eventStart).getTime() / 1000L));
        pstmt.setInt(4, (int) (Timestamp.valueOf(eventEnd).getTime() / 1000L));
        pstmt.executeUpdate();
        return pstmt.getGeneratedKeys();
    }
}
