package io.github.ocelot.client.screen;

import io.github.ocelot.common.valuecontainer.ToggleEntry;
import io.github.ocelot.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>A simple implementation of an {@link AbstractButton} that can be used to modify {@link ToggleEntry}.</p>
 *
 * @author Ocelot
 * @since 2.2.0
 */
@OnlyIn(Dist.CLIENT)
public class ValueContainerEntryToggleImpl extends AbstractButton
{
    private final ValueContainerEntry<?> entry;
    private final ToggleEntry toggleEntry;
    private boolean toggled;

    public ValueContainerEntryToggleImpl(ValueContainerEntry<?> entry, int x, int y, int width, int height)
    {
        super(x, y, width, height, entry.getDisplayName());
        if (!(entry instanceof ToggleEntry))
            throw new IllegalStateException("Entry '" + entry + "' needs to implement ToggleEntry in order to use the TOGGLE type");
        this.toggleEntry = (ToggleEntry) entry;
        this.entry = entry;
        this.setToggled(this.toggleEntry.isToggled());
    }

    @Override
    public void onPress()
    {
        Optional<Predicate<String>> optional = this.entry.getValidator();
        String value = Boolean.toString(!this.toggled);
        if (optional.isPresent() && !optional.get().test(value))
            return;
        
        this.setToggled(!this.toggled);
        this.entry.parse(value);
    }

    /**
     * @return The entry this button modifies
     */
    public ValueContainerEntry<?> getEntry()
    {
        return entry;
    }

    /**
     * @return The entry this button modifies as a toggle specific entry
     */
    public ToggleEntry getToggleEntry()
    {
        return toggleEntry;
    }

    /**
     * @return Whether or not this button is toggled.
     */
    public boolean isToggled()
    {
        return toggled;
    }

    /**
     * Sets whether or not this button is toggled.
     *
     * @param toggled Whether or not this should be <code>yes</code> or <code>no</code>
     */
    public void setToggled(boolean toggled)
    {
        this.toggled = toggled;
        this.setMessage(toggled ? new TranslationTextComponent("gui.yes") : new TranslationTextComponent("gui.no"));
    }
}
