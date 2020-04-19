package io.github.ocelot.testmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.client.ScissorHelper;
import io.github.ocelot.client.ShapeRenderer;
import io.github.ocelot.client.screen.ValueContainerEditorScreen;
import io.github.ocelot.common.ScrollHandler;
import io.github.ocelot.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.common.valuecontainer.TextFieldEntry;
import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.common.valuecontainer.ValueContainerEntry;
import io.github.ocelot.testmod.TestMod;
import io.github.ocelot.testmod.network.TestMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class TestValueContainerEditorScreen extends ValueContainerEditorScreen
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(TestMod.MOD_ID, "textures/gui/value_container_editor.png");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    public static final int PADDING = 5;
    public static final int VALUE_HEIGHT = 35;

    private final int xSize;
    private final int ySize;
    private final ScrollHandler scrollHandler;

    public TestValueContainerEditorScreen(ValueContainer container)
    {
        super(container, () -> new StringTextComponent("Test Value Container Editor"));
        this.xSize = WIDTH;
        this.ySize = HEIGHT;
        this.scrollHandler = new ScrollHandler(null, this.getEntries().size() * VALUE_HEIGHT + Minecraft.getInstance().fontRenderer.FONT_HEIGHT, 142);
    }

    @Override
    protected void init()
    {
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);
        int screenX = (this.width - this.xSize) / 2;
        int screenY = (this.height - this.ySize) / 2;

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
                    TextFieldWidget textField = new TextFieldWidget(this.getMinecraft().fontRenderer, screenX + PADDING, screenY + PADDING + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT + VALUE_HEIGHT - 20, this.xSize - PADDING * 2, 20, this.getFormattedEntryNames().getOrDefault(entry, "missingno"));
                    textField.setMaxStringLength(Integer.MAX_VALUE);
                    textField.setText(entry.getDisplay());
                    textField.setResponder(text ->
                    {
                        if (entry.isValid(text))
                            entry.parse(text);
                    });
                    validator.ifPresent(textField::setValidator);
                    //                    this.addButton(textField);
                    break;
                }
                case TOGGLE:
                {
                    //                    this.addButton(new ValueContainerEntryButtonImpl(entry, screenX + PADDING, screenY + PADDING + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT + VALUE_HEIGHT - 20, this.xSize - PADDING * 2, 20));
                    break;
                }
                case SLIDER:
                {
                    //                    this.addButton(new ValueContainerEntrySliderImpl(entry, screenX + PADDING, screenY + PADDING + this.getMinecraft().fontRenderer.FONT_HEIGHT + i * VALUE_HEIGHT + VALUE_HEIGHT - 20, this.xSize - PADDING * 2, 20));
                    break;
                }
            }
        }
    }

    @Override
    protected void sendDataToServer()
    {
        TestMessageHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(), new SyncValueContainerMessage(this.getContainer(), this.getEntries()));
    }

    @Override
    protected void renderBackground(int mouseX, int mouseY, float partialTicks)
    {
        float screenX = (this.width - this.xSize) / 2f;
        float screenY = (this.height - this.ySize) / 2f;
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        ShapeRenderer.drawRectWithTexture(screenX, screenY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void renderForeground(int mouseX, int mouseY, float partialTicks)
    {
        this.scrollHandler.scroll(-0.25f);
        RenderSystem.pushMatrix();
        RenderSystem.translated((this.width - this.xSize) / 2f, (this.height - this.ySize) / 2f, 0);
        {
            ScissorHelper.enableScissor();
            ScissorHelper.push((this.width - this.xSize) / 2f + 6, (this.height - this.ySize) / 2f + 18, 148, 142);

            float scroll = this.scrollHandler.getInterpolatedScroll(partialTicks);

            this.getMinecraft().fontRenderer.drawString(this.getFormattedTitle(), (this.xSize - this.getMinecraft().fontRenderer.getStringWidth(this.getFormattedTitle())) / 2f, 6f, 4210752);

            for (int i = 0; i < this.getEntries().size(); i++)
            {
                float y = 2 + i * VALUE_HEIGHT - scroll;
                if(y >= 142)
                    break;
                if(y + VALUE_HEIGHT < 0)
                    continue;
                ValueContainerEntry<?> entry = this.getEntries().get(i);
                this.getMinecraft().fontRenderer.drawString(this.getFormattedEntryNames().getOrDefault(entry, "missingno"), 8, 18 + y, 4210752);
            }

            ScissorHelper.pop();
            ScissorHelper.disableScissor();

            this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            boolean hasScroll = this.scrollHandler.getMaxScroll() > 0;
            float scrollbarY = hasScroll ? 127 * (scroll / this.scrollHandler.getMaxScroll()) : 0;
            ShapeRenderer.drawRectWithTexture(158, 18 + scrollbarY, hasScroll ? 176 : 188, 0, 12, 15);
        }
        RenderSystem.popMatrix();
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
}
