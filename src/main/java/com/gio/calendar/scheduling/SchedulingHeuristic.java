package com.gio.calendar.scheduling;

import com.gio.calendar.models.CalendarEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.Function;

public abstract class SchedulingHeuristic implements Function<SchedulingDetails, Optional<List<CalendarEvent>>> {
}
