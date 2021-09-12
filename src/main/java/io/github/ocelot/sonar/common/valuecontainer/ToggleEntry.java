package io.github.ocelot.sonar.common.valuecontainer;

/**
 * <p>Specifies that a {@link ValueContainerEntry} should have a toggled button initially for toggle buttons if used.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 * @deprecated TODO remove in 7.0.0
 */
public interface ToggleEntry
{
    /**
     * @return Whether this entry is toggled
     */
    boolean isToggled();
}
