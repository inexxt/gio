package com.gio.calendar.utilities;

public class TimeIntervalStringHandler {
    public static boolean checkTimeIntervalString(String targetTimeIntervalString) {
        int stringLength = targetTimeIntervalString.length();

        if(stringLength <= 1) {
            return false;
        }

        char unitCharacter = targetTimeIntervalString.charAt(stringLength - 1);

        if(unitCharacter != 'D' && unitCharacter != 'W' &&
                unitCharacter != 'M' && unitCharacter != 'Y') {
            return false;
        }

        String integerString = targetTimeIntervalString.substring(0, stringLength - 1);
        Integer numberOfUnits;

        try {
            numberOfUnits = Integer.parseInt(integerString);
        }
        catch(NumberFormatException e) {
            /* Bad integer format */
            return false;
        }

        if(numberOfUnits.intValue() <= 0) {
            return false;
        }

        return true;
    }

    public static int getTimeUnitsNumber(String targetTimeIntervalString) {
        int stringLength = targetTimeIntervalString.length();
        if (stringLength > 0)
            return Integer.parseInt(targetTimeIntervalString.substring(0, stringLength - 1));
        return 0; // doesn't matter what is the value if the field is empty
    }

    public static char getTimeUnitType(String targetTimeIntervalString) {
        if (targetTimeIntervalString.length() > 0)
            return targetTimeIntervalString.charAt(targetTimeIntervalString.length() - 1);
        return '_'; // doesn't matter what is the unit if the field is empty
    }
}