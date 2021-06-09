package com.gio.calendar.utilities;

public class TimeIntervalStringHandler {
    /**
     * Parses time interval string to check whether its representation is correct
     * @param targetTimeIntervalString - string to be checked
     * @return true if passed string contains correct representation of time interval
     * and false in other case
     */
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

    /**
     * Extracts the number of time units from given string
     * @param targetTimeIntervalString - string representation of time interval
     * @return integer number of time units
     */
    public static int getTimeUnitsNumber(String targetTimeIntervalString) {
        int stringLength = targetTimeIntervalString.length();
        if (stringLength > 0)
            return Integer.parseInt(targetTimeIntervalString.substring(0, stringLength - 1));
        return 0; // doesn't matter what is the value if the field is empty
    }


    /**
     * Extracts the character which represents time unit type from given string
     * @param targetTimeIntervalString - string representation of time interval
     * @return time unit character
     */
    public static char getTimeUnitType(String targetTimeIntervalString) {
        if (targetTimeIntervalString.length() > 0)
            return targetTimeIntervalString.charAt(targetTimeIntervalString.length() - 1);
        return '_'; // doesn't matter what is the unit if the field is empty
    }
}