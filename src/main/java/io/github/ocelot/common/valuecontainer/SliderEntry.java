package io.github.ocelot.common.valuecontainer;

/**
 * <p>Specifies that a {@link ValueContainerEntry} should use a custom percentage for sliders if used.</p>
 *
 * @author Ocelot
 */
public interface SliderEntry
{
    /**
     * @return The value of the slider
     */
    double getSliderValue();

    /**
     * @return The minimum value of the slider
     */
    double getMinSliderValue();

    /**
     * @return The maximum value of the slider
     */
    double getMaxSliderValue();

    /**
     * @return Whether or not to show a percentage on the slider
     */
    boolean isPercentage();

    /**
     * @return Whether or not to show decimal values on the slider when not showing a percentage
     */
    boolean isDecimal();
}
