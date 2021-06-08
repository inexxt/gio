package com.gio.calendar.scheduling;

import java.time.LocalDate;

/**
 * All the details that are needed to schedule events for a task.
 */
public class SchedulingDetails {
    // just a dataclass, no need for specifying getters/setters

    public LocalDate startDay;
    public LocalDate endDay;
    public int duration;
    public int maximalContinousDuration;
    public int minimalContinousDuration;
    public String eventName;
    public String eventDescription;
    public String eventTags;

    public SchedulingDetails(LocalDate startDay,
                             LocalDate endDay,
                             int duration,
                             int maximalContinousDuration,
                             int minimalContinousDuration,
                             String eventName,
                             String eventDescription,
                             String eventTags) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.duration = duration;
        this.maximalContinousDuration = maximalContinousDuration;
        this.minimalContinousDuration = minimalContinousDuration;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventTags = eventTags;
    }
}
