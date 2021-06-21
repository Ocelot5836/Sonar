package io.github.ocelot.sonar.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

    public ValueContainerEditorScreen(ValueContainer container, BlockPos pos)
    {
        this(container, pos, () -> new TranslatableComponent("screen." + Sonar.DOMAIN + ".value_container"));
    }

    @Deprecated
    public ValueContainerEditorScreen(ValueContainer container, BlockPos pos, Supplier<Component> defaultTitle)
    {
        super(container.getTitle(Objects.requireNonNull(Minecraft.getInstance().level), pos).orElseGet(defaultTitle));
        this.container = container;
        this.pos = pos;
        this.entries = container.getEntries(Minecraft.getInstance().level, pos);
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
    protected abstract void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);

    /**
     * Draws the foreground of the screen and any elements that should be drawn in front of buttons.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     * @param partialTicks The percentage from last tick and this tick
     */
    protected abstract void renderForeground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);

    /**
     * Ticks the specified child element if it needs ticking.
     *
     * @param child The child to tick
     */
    protected void tickChild(GuiEventListener child)
    {
        if (child instanceof EditBox)
        {
            ((EditBox) child).tick();
        }
    }

    /**
     * @return Whether or not this screen is able to stay open
     */
    protected boolean shouldStayOpen()
    {
        return this.minecraft == null || this.minecraft.level == null || this.minecraft.level.getBlockState(this.pos).getBlock() instanceof ValueContainer || this.minecraft.level.getBlockEntity(this.pos) instanceof ValueContainer;
    }

    @Override
    public void tick()
    {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.level == null)
            return;

        this.children.forEach(this::tickChild);

        if (!this.shouldStayOpen())
        {
            this.minecraft.player.closeContainer();
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.minecraft == null)
            return;

        // Fixes the partial ticks actually being the tick length
        partialTicks = this.getMinecraft().getFrameTime();

        super.renderBackground(matrixStack);
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        this.renderWidgets(matrixStack, mouseX, mouseY, partialTicks);
        this.renderForeground(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderWidgets(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        for (AbstractWidget button : this.buttons)
        {
            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void removed()
    {
        if (this.entries.stream().anyMatch(ValueContainerEntry::isDirty))
            this.sendDataToServer();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.minecraft == null || this.minecraft.player == null)
            return super.keyPressed(keyCode, scanCode, modifiers);
        if (super.keyPressed(keyCode, scanCode, modifiers) || this.getFocused() != null)
            return true;

        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (keyCode == 256 || this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey))
        {
            this.minecraft.player.closeContainer();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (!this.getChildAt(mouseX, mouseY).isPresent() || !super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            if (this.getFocused() != null && !this.getFocused().isMouseOver(mouseX, mouseY))
            {
                this.setFocused(null);
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
