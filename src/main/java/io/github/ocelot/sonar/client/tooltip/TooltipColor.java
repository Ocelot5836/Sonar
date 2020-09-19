package io.github.ocelot.sonar.client.tooltip;

import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * <p>Used to determine the properties of tooltips specified for items.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface TooltipColor
{
    /**
     * @return A new registry wrapper of this tooltip color
     */
    default RegistryWrapper wrap()
    {
        return new RegistryWrapper(this);
    }

    /**
     * @return The color of the border start
     */
    int getBorderStartColor();

    /**
     * @return The color of the border end
     */
    int getBorderEndColor();

    /**
     * @return The color of the background
     */
    int getBackgroundColor();

    /**
     * <p>A wrapper for {@link TooltipColor} that can be registered. This implements {@link TooltipColor} so the interface can be used instead of the registry object.</p>
     *
     * @author Ocelot
     */
    class RegistryWrapper extends ForgeRegistryEntry<RegistryWrapper> implements TooltipColor
    {
        private final TooltipColor parent;

        private RegistryWrapper(TooltipColor parent)
        {
            this.parent = parent;
        }

        @Override
        public int getBorderStartColor()
        {
            return this.parent.getBorderStartColor();
        }

        @Override
        public int getBorderEndColor()
        {
            return this.parent.getBorderEndColor();
        }

        @Override
        public int getBackgroundColor()
        {
            return this.parent.getBackgroundColor();
        }
    }
}
