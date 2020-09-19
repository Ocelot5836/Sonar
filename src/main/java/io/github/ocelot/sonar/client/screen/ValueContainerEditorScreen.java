package io.github.ocelot.sonar.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;
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
    private final BlockPos pos;
    private final List<ValueContainerEntry<?>> entries;
    private final String formattedTitle;

    public ValueContainerEditorScreen(ValueContainer container, BlockPos pos, Supplier<ITextComponent> defaultTitle)
    {
        super(container.getTitle(Objects.requireNonNull(Minecraft.getInstance().world), pos).orElseGet(defaultTitle));
        this.container = container;
        this.pos = pos;
        this.entries = container.getEntries(Minecraft.getInstance().world, pos);
        this.formattedTitle = this.getTitle().getString();
    }

    /**
     * Syncs the data in this screen with the server. Should send a message to the server actually modifying the data.
     */
    protected abstract void sendDataToServer();

    /**
     * Draws the background of the screen and any elements that should be drawn behind buttons.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    protected abstract void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);

    /**
     * Draws the foreground of the screen and any elements that should be drawn in front of buttons.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    protected abstract void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);

    /**
     * Ticks the specified child element if it needs ticking.
     *
     * @param child The child to tick
     */
    protected void tickChild(IGuiEventListener child)
    {
        if (child instanceof TextFieldWidget)
        {
            ((TextFieldWidget) child).tick();
        }
    }

    /**
     * @return Whether or not this screen is able to stay open
     */
    protected boolean shouldStayOpen()
    {
        return this.minecraft == null || this.minecraft.world == null || this.minecraft.world.getBlockState(this.pos).getBlock() instanceof ValueContainer || this.minecraft.world.getTileEntity(this.pos) instanceof ValueContainer;
    }

    @Override
    public void tick()
    {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.world == null)
            return;

        this.children.forEach(this::tickChild);

        if (!this.shouldStayOpen())
        {
            this.minecraft.player.closeScreen();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.minecraft == null)
            return;

        // Fixes the partial ticks actually being the tick length
        partialTicks = this.getMinecraft().getRenderPartialTicks();

        super.renderBackground(matrixStack);
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        this.renderWidgets(matrixStack, mouseX, mouseY, partialTicks);
        this.renderForeground(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderWidgets(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        for (Widget button : this.buttons)
        {
            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void onClose()
    {
        if (this.entries.stream().anyMatch(ValueContainerEntry::isDirty))
            this.sendDataToServer();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.minecraft == null || this.minecraft.player == null)
            return super.keyPressed(keyCode, scanCode, modifiers);
        if (super.keyPressed(keyCode, scanCode, modifiers) || this.getListener() != null)
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
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (!this.getEventListenerForPos(mouseX, mouseY).isPresent() || !super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            if (this.getListener() != null && !this.getListener().isMouseOver(mouseX, mouseY))
            {
                this.setListener(null);
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * @return The container being edited
     */
    public ValueContainer getContainer()
    {
        return container;
    }

    /**
     * @return The position of the container being edited
     */
    public BlockPos getPos()
    {
        return pos;
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
}
