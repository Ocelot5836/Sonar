package io.github.ocelot.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.client.FontHelper;
import io.github.ocelot.client.ScissorHelper;
import io.github.ocelot.client.ShapeRenderer;
import io.github.ocelot.common.ScrollHandler;
import io.github.ocelot.common.valuecontainer.TextFieldEntry;
import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
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
    public static final double MAX_SCROLL = 2f;
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    public static final int VALUE_HEIGHT = 35;

    private final int xSize;
    private final int ySize;
    private final ScrollHandler scrollHandler;

    private boolean scrolling;

    public ValueContainerEditorScreenImpl(ValueContainer container, BlockPos pos, Supplier<ITextComponent> defaultTitle)
    {
        super(container, pos, defaultTitle);
        this.xSize = WIDTH;
        this.ySize = HEIGHT;
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
            FontHelper.drawString(this.getMinecraft().fontRenderer, this.getFormattedEntryNames().getOrDefault(entry, "missingno"), 8, 18 + y, -1, true);
        }
    }

    @Override
    protected void init()
    {
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);

        for (int i = 0; i < this.getEntries().size(); i++)
        {
            ValueContainerEntry<?> entry = this.getEntries().get(i);
            switch (entry.getInputType())
            {
                case TEXT_FIELD:
                {
                    Optional<Predicate<String>> validator = Optional.empty();
                    if (entry instanceof TextFieldEntry)
                        validator = ((TextFieldEntry) entry).getValidator();
                    TextFieldWidget textField = new TextFieldWidget(this.getMinecraft().fontRenderer, 8, 22 + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT, 144, 20, this.getFormattedEntryNames().getOrDefault(entry, "missingno"));
                    textField.setMaxStringLength(Integer.MAX_VALUE);
                    textField.setText(entry.getDisplay());
                    textField.setResponder(text ->
                    {
                        if (entry.isValid(text))
                            entry.parse(text);
                    });
                    validator.ifPresent(textField::setValidator);
                    this.addButton(textField);
                    break;
                }
                case TOGGLE:
                {
                    this.addButton(new ValueContainerEntryButtonImpl(entry, 8, 22 + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
                case SLIDER:
                {
                    this.addButton(new ValueContainerEntrySliderImpl(entry, 8, 22 + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT, 144, 20));
                    break;
                }
            }
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
        for (Widget button : this.buttons)
        {
            if (button.y - scroll + button.getHeight() < 0)
                continue;
            if (button.y - scroll >= 160)
                break;
            button.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBackground(int mouseX, int mouseY, float partialTicks)
    {
        float screenX = (this.width - this.xSize) / 2f;
        float screenY = (this.height - this.ySize) / 2f;
        this.getMinecraft().getTextureManager().bindTexture(this.getBackgroundTextureLocation());
        ShapeRenderer.drawRectWithTexture(screenX, screenY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void renderForeground(int mouseX, int mouseY, float partialTicks)
    {
        this.getMinecraft().fontRenderer.drawString(this.getFormattedTitle(), (this.xSize - this.getMinecraft().fontRenderer.getStringWidth(this.getFormattedTitle())) / 2f, 6f, 4210752);

        this.getMinecraft().getTextureManager().bindTexture(this.getBackgroundTextureLocation());
        boolean hasScroll = this.scrollHandler.getMaxScroll() > 0;
        float scrollbarY = hasScroll ? 127 * (this.scrollHandler.getInterpolatedScroll(partialTicks) / this.scrollHandler.getMaxScroll()) : 0;
        ShapeRenderer.drawRectWithTexture(158, 18 + scrollbarY, hasScroll ? 176 : 188, 0, 12, 15);
    }

    @Override
    public void tick()
    {
        super.tick();
        this.scrollHandler.update();
    }

    @Override
    public void removed()
    {
        super.removed();
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public Optional<IGuiEventListener> getEventListenerForPos(double mouseX, double mouseY)
    {
        float scroll = this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks());
        return mouseX >= 6 && mouseX < 148 && mouseY - scroll >= 18 && mouseY - scroll < 159 ? super.getEventListenerForPos(mouseX, mouseY) : Optional.empty();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        mouseX -= (this.width - this.xSize) / 2f;
        mouseY -= (this.height - this.ySize) / 2f;
        if (super.mouseClicked(mouseX, mouseY + this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton))
            return true;
        if (this.scrollHandler.getMaxScroll() > 0 && mouseX >= 158 && mouseX < 169 && mouseY >= 18 && mouseY < 160)
        {
            this.scrolling = true;
            this.scrollHandler.setScroll(this.scrollHandler.getMaxScroll() * (float) MathHelper.clamp((mouseY - 25) / 128.0, 0.0, 1.0));
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        mouseX -= (this.width - this.xSize) / 2f;
        mouseY -= (this.height - this.ySize) / 2f;
        this.scrolling = false;
        return super.mouseReleased(mouseX, mouseY + this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        mouseX -= (this.width - this.xSize) / 2f;
        mouseY -= (this.height - this.ySize) / 2f;
        if (super.mouseDragged(mouseX, mouseY + this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), mouseButton, deltaX, deltaY))
            return true;
        if (this.scrollHandler.getMaxScroll() > 0 && this.scrolling)
            this.scrollHandler.setScroll(this.scrollHandler.getMaxScroll() * (float) MathHelper.clamp((mouseY - 25) / 128.0, 0.0, 1.0));
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        mouseX -= (this.width - this.xSize) / 2f;
        mouseY -= (this.height - this.ySize) / 2f;
        if (super.mouseScrolled(mouseX, mouseY + this.scrollHandler.getInterpolatedScroll(Minecraft.getInstance().getRenderPartialTicks()), amount))
            return true;
        return this.scrollHandler.mouseScrolled(MAX_SCROLL, amount);
    }

    /**
     * @return The location of the image that should be used for the background of the screen
     */
    public abstract ResourceLocation getBackgroundTextureLocation();
}
