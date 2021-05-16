package org.simondoesstuff.plugindonotdisturb;

import java.util.List;

public class DNDUtils {
    /**
     * Searches an enum for a specific value while being case insensitive.
     *
     */
    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration,
                                                   String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(search)) {
                return each;
            }
        }

        return null;
    }

    /**
     *  Searches a list of enums for a specific value while being case insensitive.
     */
    public static <T extends Enum<?>> T searchEnumValues(List<T> values, String search) {
        for (T value : values) {
            if (value.name().equalsIgnoreCase(search)) {
                return value;
            }
        }

        return null;
    }
}
