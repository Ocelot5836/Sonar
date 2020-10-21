package io.github.ocelot.sonar.common.util;

/**
 * <p>Utilities helping with converting time into strings.</p>
 *
 * @author Ocelot
 * @since 3.0.0
 */
public final class TimeUtils
{
    /**
     * Converts Minecraft world time to a 24 hour format.
     *
     * @param time The current world time
     * @return The formatted time
     */
    public static String timeToString(long time, boolean simple)
    {
        int hours = (int) ((Math.floor(time / 1000.0) + 6) % 24);
        int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
        String value = String.format("%02d:%02d", simple ? hours % 12 : hours, minutes);
        if (simple)
            value += " " + (hours / 12 > 0 ? "PM" : "AM");
        return value;
    }
}
