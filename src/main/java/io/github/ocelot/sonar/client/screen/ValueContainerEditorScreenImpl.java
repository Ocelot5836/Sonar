package io.github.ocelot.sonar.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.client.render.ShapeRenderer;
import io.github.ocelot.sonar.client.util.ScissorHelper;
import io.github.ocelot.sonar.common.util.ScrollHandler;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
    private final List<AbstractWidget> entryWidgets;
    private final ScrollHandler scrollHandler;

    private boolean scrolling;

    public ValueContainerEditorScreenImpl(ValueContainer container, BlockPos pos)
    {
        super(container, pos);
        this.xSize = WIDTH;
        this.ySize = HEIGHT;
        this.entryWidgets = new ArrayList<>();
        this.scrollHandler = new ScrollHandler(this.getEntries().size() * VALUE_HEIGHT, 142);
        this.scrollHandler.setScrollSpeed((float) this.scrollHandler.getMaxScroll() / (float) this.getEntries().size());

        this.scrolling = false;
    }

    private void renderLabels(PoseStack matrixStack, float partialTicks)
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
            this.getMinecraft().font.drawShadow(matrixStack, entry.getDisplayName().getString(), 8, 18 + y, -1);
        }
    }

    @Override
    protected void init()
    {
        this.entryWidgets.clear();
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(true);

        this.addRenderableWidget(new Button((this.width - this.xSize) / 2, (this.height + this.ySize) / 2 + 4, this.xSize, 20, new TranslatableComponent("gui.done"), button -> this.getMinecraft().setScreen(null)));

        for (int i = 0; i < this.getEntries().size(); i++)
        {
            ValueContainerEntry<?> entry = this.getEntries().get(i);
            switch (entry.getInputType())
            {
                case TEXT_FIELD:
                {
                    Optional<Predicate<String>> optional = entry.getValidator();
                    EditBox textField = new EditBox(this.getMinecraft().font, 8, 22 + this.getMinecraft().font.lineHeight + i * VALUE_HEIGHT, 144, 20, new TextComponent(""));
                    textField.setMaxLength(Integer.MAX_VALUE);
                    textField.setValue(entry.getDisplay());
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
                    this.entryWidgets.add(new ValueContainerEntryToggleImpl(entry, 8, 22 + this.getMinecraft().font.lineHeight + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
                case SWITCH:
                {
                    this.entryWidgets.add(new ValueContainerEntrySwitchImpl(entry, 8, 22 + this.getMinecraft().font.lineHeight + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
                case SLIDER:
                {
                    this.entryWidgets.add(new ValueContainerEntrySliderImpl(entry, 8, 22 + this.getMinecraft().font.lineHeight + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
            }
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

        matrixStack.pushPose();
        matrixStack.translate((this.width - this.xSize) / 2f, (this.height - this.ySize) / 2f, 0);
        {
            matrixStack.pushPose();
            matrixStack.translate(0, -this.scrollHandler.getInterpolatedScroll(partialTicks), 0);
            {
                ScissorHelper.push((this.width - this.xSize) / 2f + 6, (this.height - this.ySize) / 2f + 18, 148, 142);
                this.renderWidgets(matrixStack, mouseX - (int) ((this.width - this.xSize) / 2f), mouseY - (int) ((this.height - this.ySize) / 2f) + (int) this.scrollHandler.getInterpolatedScroll(partialTicks), partialTicks);
                this.renderLabels(matrixStack, partialTicks);
                ScissorHelper.pop();
            }
            matrixStack.popPose();
            this.renderForeground(matrixStack, mouseX - (int) ((this.width - this.xSize) / 2f), mouseY - (int) ((this.height - this.ySize) / 2f), partialTicks);
        }
        matrixStack.popPose();
    }

    @Override
    public void renderWidgets(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        float scroll = this.scrollHandler.getInterpolatedScroll(partialTicks);
        for (AbstractWidget widget : this.entryWidgets)
        {
            if (widget.y - scroll + widget.getHeight() < 0)
                continue;
            if (widget.y - scroll >= 160)
                break;
            widget.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        float screenX = (this.width - this.xSize) / 2f;
        float screenY = (this.height - this.ySize) / 2f;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        ShapeRenderer.drawRectWithTexture(matrixStack, screenX, screenY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void renderForeground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.getMinecraft().font.draw(matrixStack, this.getFormattedTitle(), (this.xSize - this.getMinecraft().font.width(this.getFormattedTitle())) / 2f, 6f, 4210752);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        boolean hasScroll = this.scrollHandler.getMaxScroll() > 0;
        float scrollbarY = hasScroll ? 127 * (this.scrollHandler.getInterpolatedScroll(partialTicks) / this.scrollHandler.getMaxScroll()) : 0;
        ShapeRenderer.drawRectWithTexture(matrixStack, 158, 18 + scrollbarY, hasScroll ? 176 : 188, 0, 12, 15);
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
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public Optional<GuiEventListener> getEntryWidgetForPos(double mouseX, double mouseY)
    {
        mouseX -= (this.width - this.xSize) / 2f;
        mouseY -= (this.height - this.ySize) / 2f;
        float scroll = this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getFrameTime());
        if (mouseX >= 6 && mouseX < 148 && mouseY + scroll >= 18 && mouseY + scroll < 159)
        {
            for (GuiEventListener iguieventlistener : this.entryWidgets)
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
        for (GuiEventListener iguieventlistener : this.children())
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
        float scroll = this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getFrameTime());
        for (GuiEventListener iguieventlistener : this.entryWidgets)
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
            this.scrollHandler.setScroll(this.scrollHandler.getMaxScroll() * (float) Mth.clamp((mouseY - 25) / 128.0, 0.0, 1.0));
            return true;
        }

        return false;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener)
    {
        for (AbstractWidget entryWidget : this.entryWidgets)
            if (entryWidget != listener && entryWidget instanceof EditBox)
                ((EditBox) entryWidget).setFocus(false);
        super.setFocused(listener);
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
        GuiEventListener focused = this.getFocused();
        if (focused != null && this.isDragging() && mouseButton == 0)
        {
            if (focused instanceof AbstractWidget && this.entryWidgets.contains(focused))
                return focused.mouseDragged(mouseX - (this.width - this.xSize) / 2f, mouseY - (this.height - this.ySize) / 2f, mouseButton, deltaX, deltaY);
            if (super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY))
                return true;
        }
        if (this.scrollHandler.getMaxScroll() > 0 && this.scrolling)
            this.scrollHandler.setScroll(this.scrollHandler.getMaxScroll() * (float) Mth.clamp((mouseY - (this.height - this.ySize) / 2f - 25) / 128.0, 0.0, 1.0));
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (super.mouseScrolled(mouseX, mouseY, amount))
            return true;
        return this.getChildAt(mouseX, mouseY).filter(iguieventlistener -> iguieventlistener.mouseScrolled(mouseX - (this.width - this.xSize) / 2f, mouseY - (this.height - this.ySize) / 2f, amount)).isPresent() || this.scrollHandler.mouseScrolled(MAX_SCROLL, amount);
    }

    @Override
    public boolean changeFocus(boolean p_changeFocus_1_)
    {
        GuiEventListener iguieventlistener = this.getFocused();
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

    private boolean changeFocus(boolean p_changeFocus_1_, List<? extends GuiEventListener> list, @Nullable GuiEventListener focused)
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

        ListIterator<? extends GuiEventListener> listiterator = list.listIterator(i);
        BooleanSupplier booleansupplier = p_changeFocus_1_ ? listiterator::hasNext : listiterator::hasPrevious;
        Supplier<? extends GuiEventListener> supplier = p_changeFocus_1_ ? listiterator::next : listiterator::previous;

        while (booleansupplier.getAsBoolean())
        {
            GuiEventListener iguieventlistener1 = supplier.get();
            if (iguieventlistener1.changeFocus(p_changeFocus_1_))
            {
                this.setFocused(iguieventlistener1);
                return true;
            }
        }
        return false;
    }
}
