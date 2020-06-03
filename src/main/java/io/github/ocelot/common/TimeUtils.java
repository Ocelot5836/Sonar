package io.github.ocelot.common;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * <p>Utilities helping with converting time into strings.</p>
 *
 * @author Ocelot
 * @since 3.0.0
 */
@SuppressWarnings("unused")
public final class TimeUtils
{
    /**
     * Abbreviates the provided time into the largest unit over zero.
     *
     * @param time     The time to abbreviate
     * @param timeUnit The unit to abbreviate
     * @return A double value of how many of the largest unit the time can be represented in
     */
    public static double abbreviateLargestUnit(long time, TimeUnit timeUnit)
    {
        long nanos = timeUnit.toNanos(time);
        TimeUnit unit = getLargestUnit(time, timeUnit);
        return (double) nanos / NANOSECONDS.convert(1, unit);
    }

    /**
     * Turns the provided time into the largest unit where the time is above zero.
     *
     * @param time     The time to abbreviate
     * @param timeUnit The unit to abbreviate
     * @return The largest unit of time that can nicely
     */
    public static TimeUnit getLargestUnit(long time, TimeUnit timeUnit)
    {
        long nanos = timeUnit.toNanos(time);
        if (DAYS.convert(nanos, NANOSECONDS) > 0)
        {
            return DAYS;
        }
        if (HOURS.convert(nanos, NANOSECONDS) > 0)
        {
            return HOURS;
        }
        if (MINUTES.convert(nanos, NANOSECONDS) > 0)
        {
            return MINUTES;
        }
        if (SECONDS.convert(nanos, NANOSECONDS) > 0)
        {
            return SECONDS;
        }
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0)
        {
            return MILLISECONDS;
        }
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0)
        {
            return MICROSECONDS;
        }
        return NANOSECONDS;
    }

    /**
     * Uses the provided {@link TimeUnit} to create an abbreviation of the time unit as a char sequence.
     *
     * @param timeUnit The unit to abbreviate
     * @return The time unit as a char sequence
     */
    public static CharSequence abbreviate(TimeUnit timeUnit)
    {
        switch (timeUnit)
        {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "\u03bcs"; // μs
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new AssertionError();
        }
    }
}
