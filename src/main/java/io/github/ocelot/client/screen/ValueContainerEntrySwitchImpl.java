package io.github.ocelot.client.screen;

import io.github.ocelot.common.valuecontainer.SwitchEntry;
import io.github.ocelot.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * <p>A simple implementation of an {@link AbstractButton} that can be used to modify {@link SwitchEntry}.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
@OnlyIn(Dist.CLIENT)
public class ValueContainerEntrySwitchImpl extends AbstractButton
{
    private final ValueContainerEntry<?> entry;
    private final SwitchEntry switchEntry;

    public ValueContainerEntrySwitchImpl(ValueContainerEntry<?> entry, int x, int y, int width, int height)
    {
        super(x, y, width, height, entry.getDisplayName());
        if (!(entry instanceof SwitchEntry))
            throw new IllegalStateException("Entry '" + entry + "' needs to implement ToggleEntry in order to use the SWITCH type");
        this.switchEntry = (SwitchEntry) entry;
        this.entry = entry;
        this.setMessage(new StringTextComponent(this.entry.getDisplay()));
    }

    private void onPress(int mouseButton)
    {
        if (mouseButton == 0)
            this.switchEntry.showNext();
        if (mouseButton == 1)
            this.switchEntry.showPrevious();
        this.setMessage(new StringTextComponent(this.entry.getDisplay()));
    }

    @Override
    public void onPress()
    {
        this.onPress(0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (!this.active || !this.visible)
            return false;
        if (this.isValidClickButton(mouseButton))
        {
            if (this.clicked(mouseX, mouseY))
            {
                this.playDownSound(Minecraft.getInstance().getSoundHandler());
                this.onPress(mouseButton);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int mouseButton)
    {
        return mouseButton == 0 || mouseButton == 1;
    }

    /**
     * @return The entry this button modifies
     */
    public ValueContainerEntry<?> getEntry()
    {
        return entry;
    }

    /**
     * @return The entry this button modifies as a switch specific entry
     */
    public SwitchEntry getSwitchEntry()
    {
        return switchEntry;
    }
}
