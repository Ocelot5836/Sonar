package io.github.ocelot.client.screen;

import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>A simple template that can be used for general {@link ValueContainer} editing screens.</p>
 *
 * @author Ocelot
 * @see ValueContainer
 * @since 2.2.0
 */
@OnlyIn(Dist.CLIENT)
public abstract class ValueContainerEditorScreen extends Screen
{
    private final ValueContainer container;
    private final List<ValueContainerEntry<?>> entries;
    private final String formattedTitle;
    private final Map<ValueContainerEntry<?>, String> formattedEntryNames;

    public ValueContainerEditorScreen(ValueContainer container, Supplier<ITextComponent> defaultTitle)
    {
        super(container.getTitle().orElseGet(defaultTitle));
        this.container = container;
        this.entries = container.getEntries();
        this.formattedTitle = this.getTitle().getFormattedText();
        this.formattedEntryNames = new HashMap<>();
        this.entries.forEach(entry -> this.formattedEntryNames.put(entry, entry.getDisplayName().getFormattedText()));
    }

    /**
     * Syncs the data in this screen with the server. Should send a message to the server actually modifying the data.
     */
    protected abstract void sendDataToServer();

    /**
     * Draws the background of the screen and any elements that should be drawn behind buttons.
     *
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    protected abstract void renderBackground(int mouseX, int mouseY, float partialTicks);

    /**
     * Draws the foreground of the screen and any elements that should be drawn in front of buttons.
     *
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    protected abstract void renderForeground(int mouseX, int mouseY, float partialTicks);

    @Override
    public void tick()
    {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.world == null)
            return;

        this.children.forEach(child ->
        {
            if (child instanceof TextFieldWidget)
            {
                ((TextFieldWidget) child).tick();
            }
        });

        if (!(this.minecraft.world.getTileEntity(this.container.getContainerPos()) instanceof ValueContainer))
        {
            this.minecraft.player.closeScreen();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.minecraft == null)
            return;

        // Fixes the partial ticks actually being the tick length
        partialTicks = this.getMinecraft().getRenderPartialTicks();

        super.renderBackground();
        this.renderBackground(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
        this.renderForeground(mouseX, mouseY, partialTicks);
    }

    @Override
    public void removed()
    {
        this.sendDataToServer();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.minecraft == null || this.minecraft.player == null)
            return super.keyPressed(keyCode, scanCode, modifiers);
        if (super.keyPressed(keyCode, scanCode, modifiers) || this.getFocused() != null)
            return true;

        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (keyCode == 256 || this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))
        {
            this.minecraft.player.closeScreen();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        if (!(this.getFocused() instanceof TextFieldWidget))
            this.setFocused(null);
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (this.getFocused() != null && !this.getFocused().isMouseOver(mouseX, mouseY))
        {
            this.setFocused(null);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * @return The container being edited
     */
    public ValueContainer getContainer()
    {
        return container;
    }

    /**
     * @return The list of entries fetched from the container
     */
    public List<ValueContainerEntry<?>> getEntries()
    {
        return entries;
    }

    /**
     * @return The title of the window as raw text
     */
    public String getFormattedTitle()
    {
        return formattedTitle;
    }

    /**
     * @return The map of cached entry names as raw text
     */
    public Map<ValueContainerEntry<?>, String> getFormattedEntryNames()
    {
        return formattedEntryNames;
    }
}
