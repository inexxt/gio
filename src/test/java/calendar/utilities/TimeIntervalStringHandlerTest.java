package calendar.utilities;

import com.gio.calendar.utilities.TimeIntervalStringHandler;

import java.sql.Time;

import static org.junit.Assert.*;

public class TimeIntervalStringHandlerTest {
    @org.junit.Test
    public void tooShortStringShouldReturnFalse() {
        assertFalse(TimeIntervalStringHandler.checkTimeIntervalString("1"));
    }

    @org.junit.Test
    public void badUnitCharacterShouldReturnFalse() {
        assertFalse(TimeIntervalStringHandler.checkTimeIntervalString("5S"));
    }

    @org.junit.Test
    public void goodUnitCharacterShouldReturnTrue() {
        assertTrue(TimeIntervalStringHandler.checkTimeIntervalString("100D"));
        assertTrue(TimeIntervalStringHandler.checkTimeIntervalString("6W"));
        assertTrue(TimeIntervalStringHandler.checkTimeIntervalString("3M"));
        assertTrue(TimeIntervalStringHandler.checkTimeIntervalString("1Y"));
    }

    @org.junit.Test
    public void badFormatOfIntegerShouldReturnFalse() {
        assertFalse(TimeIntervalStringHandler.checkTimeIntervalString("12%233W"));
        assertFalse(TimeIntervalStringHandler.checkTimeIntervalString("ABCW"));
        assertFalse(TimeIntervalStringHandler.checkTimeIntervalString("%123D"));
    }

    @org.junit.Test
    public void nonPositiveUnitsValueShouldReturnFalse() {
        assertFalse(TimeIntervalStringHandler.checkTimeIntervalString("-123Y"));
        assertFalse(TimeIntervalStringHandler.checkTimeIntervalString("0D"));
    }

    @org.junit.Test
    public void getUnitsNumberShouldWork() {
        assertEquals(3, TimeIntervalStringHandler.getTimeUnitsNumber("3Y"));
        assertEquals(17, TimeIntervalStringHandler.getTimeUnitsNumber("17W"));
    }

    @org.junit.Test
    public void getUnitTypeShouldWork() {
        assertEquals('D', TimeIntervalStringHandler.getTimeUnitType("5D"));
        assertEquals('Y', TimeIntervalStringHandler.getTimeUnitType("2Y"));
    }
}
