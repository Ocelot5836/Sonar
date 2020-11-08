package io.github.ocelot.sonar.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.client.render.ShapeRenderer;
import io.github.ocelot.sonar.client.util.ScissorHelper;
import io.github.ocelot.sonar.common.util.ScrollHandler;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>A simple scrolling implementation of {@link ValueContainerEditorScreen}. For more customizations, use {@link ValueContainerEditorScreen}.</p>
 *
 * @author Ocelot
 * @see ValueContainerEditorScreen
 * @since 2.2.0
 */
@OnlyIn(Dist.CLIENT)
public abstract class ValueContainerEditorScreenImpl extends ValueContainerEditorScreen
{
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Sonar.DOMAIN, "textures/gui/value_container_editor.png");
    public static final double MAX_SCROLL = 2f;
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    public static final int VALUE_HEIGHT = 35;

    private final int xSize;
    private final int ySize;
    private final List<Widget> entryWidgets;
    private final ScrollHandler scrollHandler;

    private boolean scrolling;

    public ValueContainerEditorScreenImpl(ValueContainer container, BlockPos pos)
    {
        super(container, pos);
        this.xSize = WIDTH;
        this.ySize = HEIGHT;
        this.entryWidgets = new ArrayList<>();
        this.scrollHandler = new ScrollHandler(null, this.getEntries().size() * VALUE_HEIGHT, 142);
        this.scrollHandler.setScrollSpeed(this.scrollHandler.getMaxScroll() / this.getEntries().size());

        this.scrolling = false;
    }

    @Deprecated
    public ValueContainerEditorScreenImpl(ValueContainer container, BlockPos pos, Supplier<ITextComponent> defaultTitle)
    {
        super(container, pos, defaultTitle);
        this.xSize = WIDTH;
        this.ySize = HEIGHT;
        this.entryWidgets = new ArrayList<>();
        this.scrollHandler = new ScrollHandler(null, this.getEntries().size() * VALUE_HEIGHT, 142);
        this.scrollHandler.setScrollSpeed(this.scrollHandler.getMaxScroll() / this.getEntries().size());

        this.scrolling = false;
    }

    private void renderLabels(float partialTicks)
    {
        float scroll = this.scrollHandler.getInterpolatedScroll(partialTicks);
        for (int i = 0; i < this.getEntries().size(); i++)
        {
            float y = 2 + i * VALUE_HEIGHT;
            if (y - scroll + VALUE_HEIGHT < 0)
                continue;
            if (y - scroll >= 160)
                break;
            ValueContainerEntry<?> entry = this.getEntries().get(i);
            this.getMinecraft().fontRenderer.drawStringWithShadow(entry.getDisplayName().getFormattedText(), 8, 18 + y, -1);
        }
    }

    @Override
    protected void init()
    {
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        this.addButton(new Button((this.width - this.xSize) / 2, (this.height + this.ySize) / 2 + 4, this.xSize, 20, I18n.format("gui.done"), button -> this.getMinecraft().displayGuiScreen(null)));

        for (int i = 0; i < this.getEntries().size(); i++)
        {
            ValueContainerEntry<?> entry = this.getEntries().get(i);
            switch (entry.getInputType())
            {
                case TEXT_FIELD:
                {
                    Optional<Predicate<String>> optional = entry.getValidator();
                    TextFieldWidget textField = new TextFieldWidget(this.getMinecraft().fontRenderer, 8, 22 + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT, 144, 20, "");
                    textField.setMaxStringLength(Integer.MAX_VALUE);
                    textField.setText(entry.getDisplay());
                    textField.setResponder(text ->
                    {
                        boolean valid = !optional.isPresent() || optional.get().test(text);
                        textField.setTextColor(valid ? 14737632 : 16733525);
                        if (valid)
                            entry.parse(text);
                    });
                    this.entryWidgets.add(textField);
                    break;
                }
                case TOGGLE:
                {
                    this.entryWidgets.add(new ValueContainerEntryToggleImpl(entry, 8, 22 + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
                case SWITCH:
                {
                    this.entryWidgets.add(new ValueContainerEntrySwitchImpl(entry, 8, 22 + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
                case SLIDER:
                {
                    this.entryWidgets.add(new ValueContainerEntrySliderImpl(entry, 8, 22 + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
            }
        }
    }

    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        this.entryWidgets.clear();
        super.init(minecraft, width, height);
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

        for (Widget widget : this.buttons)
            widget.render(mouseX, mouseY, partialTicks);

        RenderSystem.pushMatrix();
        RenderSystem.translatef((this.width - this.xSize) / 2f, (this.height - this.ySize) / 2f, 0);
        {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, -this.scrollHandler.getInterpolatedScroll(partialTicks), 0);
            {
                ScissorHelper.push((this.width - this.xSize) / 2f + 6, (this.height - this.ySize) / 2f + 18, 148, 142);
                this.renderWidgets(mouseX - (int) ((this.width - this.xSize) / 2f), mouseY - (int) ((this.height - this.ySize) / 2f) + (int) this.scrollHandler.getInterpolatedScroll(partialTicks), partialTicks);
                this.renderLabels(partialTicks);
                ScissorHelper.pop();
            }
            RenderSystem.popMatrix();
            this.renderForeground(mouseX - (int) ((this.width - this.xSize) / 2f), mouseY - (int) ((this.height - this.ySize) / 2f), partialTicks);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public void renderWidgets(int mouseX, int mouseY, float partialTicks)
    {
        float scroll = this.scrollHandler.getInterpolatedScroll(partialTicks);
        for (Widget widget : this.entryWidgets)
        {
            if (widget.y - scroll + widget.getHeight() < 0)
                continue;
            if (widget.y - scroll >= 160)
                break;
            widget.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBackground(int mouseX, int mouseY, float partialTicks)
    {
        float screenX = (this.width - this.xSize) / 2f;
        float screenY = (this.height - this.ySize) / 2f;
        this.getMinecraft().getTextureManager().bindTexture(this.getBackgroundTextureLocation());
        ShapeRenderer.drawRectWithTexture(new MatrixStack(), screenX, screenY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void renderForeground(int mouseX, int mouseY, float partialTicks)
    {
        this.getMinecraft().fontRenderer.drawString(this.getFormattedTitle(), (this.xSize - this.getMinecraft().fontRenderer.getStringWidth(this.getFormattedTitle())) / 2f, 6f, 4210752);

        this.getMinecraft().getTextureManager().bindTexture(this.getBackgroundTextureLocation());
        boolean hasScroll = this.scrollHandler.getMaxScroll() > 0;
        float scrollbarY = hasScroll ? 127 * (this.scrollHandler.getInterpolatedScroll(partialTicks) / this.scrollHandler.getMaxScroll()) : 0;
        ShapeRenderer.drawRectWithTexture(new MatrixStack(), 158, 18 + scrollbarY, hasScroll ? 176 : 188, 0, 12, 15);
    }

    @Override
    public void tick()
    {
        super.tick();
        this.entryWidgets.forEach(this::tickChild);
        this.scrollHandler.update();
    }

    @Override
    public void removed()
    {
        super.removed();
        this.getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public Optional<IGuiEventListener> getEntryWidgetForPos(double mouseX, double mouseY)
    {
        mouseX -= (this.width - this.xSize) / 2f;
        mouseY -= (this.height - this.ySize) / 2f;
        float scroll = this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks());
        if (mouseX >= 6 && mouseX < 148 && mouseY + scroll >= 18 && mouseY + scroll < 159)
        {
            for (IGuiEventListener iguieventlistener : this.entryWidgets)
            {
                if (iguieventlistener.isMouseOver(mouseX, mouseY))
                {
                    return Optional.of(iguieventlistener);
                }
            }
        }
        return Optional.empty();
    }

    private boolean componentClicked(double mouseX, double mouseY, int mouseButton)
    {
        for (IGuiEventListener iguieventlistener : this.children())
        {
            if (iguieventlistener.mouseClicked(mouseX, mouseY, mouseButton))
            {
                this.setFocused(iguieventlistener);
                if (mouseButton == 0)
                    this.setDragging(true);
                return true;
            }
        }
        return false;
    }

    private boolean entryComponentClicked(double mouseX, double mouseY, int mouseButton)
    {
        float scroll = this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks());
        for (IGuiEventListener iguieventlistener : this.entryWidgets)
        {
            if (iguieventlistener.mouseClicked(mouseX - (this.width - this.xSize) / 2f, mouseY - (this.height - this.ySize) / 2f + scroll, mouseButton))
            {
                this.setFocused(iguieventlistener);
                if (mouseButton == 0)
                    this.setDragging(true);
                return true;
            }
        }
        return false;
    }

    private boolean clickScrollbar(double mouseX, double mouseY)
    {
        mouseX -= (this.width - this.xSize) / 2f;
        mouseY -= (this.height - this.ySize) / 2f;
        if (this.scrollHandler.getMaxScroll() > 0 && mouseX >= 158 && mouseX < 169 && mouseY >= 18 && mouseY < 160)
        {
            this.scrolling = true;
            this.scrollHandler.setScroll(this.scrollHandler.getMaxScroll() * (float) MathHelper.clamp((mouseY - 25) / 128.0, 0.0, 1.0));
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean flag = false;
        if (!this.componentClicked(mouseX, mouseY, mouseButton) && !this.entryComponentClicked(mouseX, mouseY, mouseButton))
        {
            if (this.getFocused() != null && !this.getFocused().isMouseOver(mouseX, mouseY))
            {
                this.setFocused(null);
            }
            flag = true;
        }

        return !flag || this.clickScrollbar(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        this.scrolling = false;
        if (super.mouseReleased(mouseX, mouseY, mouseButton))
            return true;
        return this.getEntryWidgetForPos(mouseX, mouseY).filter(iguieventlistener -> iguieventlistener.mouseReleased(mouseX, mouseY, mouseButton)).isPresent();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        IGuiEventListener focused = this.getFocused();
        if (focused != null && this.isDragging() && mouseButton == 0)
        {
            if (focused instanceof Widget && this.entryWidgets.contains(focused))
                return focused.mouseDragged(mouseX - (this.width - this.xSize) / 2f, mouseY - (this.height - this.ySize) / 2f, mouseButton, deltaX, deltaY);
            if (super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY))
                return true;
        }
        if (this.scrollHandler.getMaxScroll() > 0 && this.scrolling)
            this.scrollHandler.setScroll(this.scrollHandler.getMaxScroll() * (float) MathHelper.clamp((mouseY - (this.height - this.ySize) / 2f - 25) / 128.0, 0.0, 1.0));
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (super.mouseScrolled(mouseX, mouseY, amount))
            return true;
        return this.getEventListenerForPos(mouseX, mouseY).filter(iguieventlistener -> iguieventlistener.mouseScrolled(mouseX - (this.width - this.xSize) / 2f, mouseY - (this.height - this.ySize) / 2f, amount)).isPresent() || this.scrollHandler.mouseScrolled(MAX_SCROLL, amount);
    }

    @Override
    public boolean changeFocus(boolean p_changeFocus_1_)
    {
        IGuiEventListener iguieventlistener = this.getFocused();
        if (iguieventlistener != null && iguieventlistener.changeFocus(p_changeFocus_1_))
        {
            return true;
        }
        else
        {
            if (changeFocus(p_changeFocus_1_, this.entryWidgets, iguieventlistener))
                return true;
            if (changeFocus(p_changeFocus_1_, this.children(), iguieventlistener))
                return true;

            this.setFocused(null);
            return false;
        }
    }

    private boolean changeFocus(boolean p_changeFocus_1_, List<? extends IGuiEventListener> list, @Nullable IGuiEventListener focused)
    {
        int j = list.indexOf(focused);
        int i;
        if (focused != null && j >= 0)
        {
            i = j + (p_changeFocus_1_ ? 1 : 0);
        }
        else if (p_changeFocus_1_)
        {
            i = 0;
        }
        else
        {
            i = list.size();
        }

        ListIterator<? extends IGuiEventListener> listiterator = list.listIterator(i);
        BooleanSupplier booleansupplier = p_changeFocus_1_ ? listiterator::hasNext : listiterator::hasPrevious;
        Supplier<? extends IGuiEventListener> supplier = p_changeFocus_1_ ? listiterator::next : listiterator::previous;

        while (booleansupplier.getAsBoolean())
        {
            IGuiEventListener iguieventlistener1 = supplier.get();
            if (iguieventlistener1.changeFocus(p_changeFocus_1_))
            {
                this.setFocused(iguieventlistener1);
                return true;
            }
        }
        return false;
    }

    /**
     * @return The location of the image that should be used for the background of the screen
     * @deprecated Not needed anymore, resources are included TODO remove in 5.1.0
     */
    public ResourceLocation getBackgroundTextureLocation()
    {
        return BACKGROUND_LOCATION;
    }
}
