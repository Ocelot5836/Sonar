package io.github.ocelot.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Reduces the view of {@link Screen} to just the tooltip rendering portion. Useful for implementations that wish to have a reduced tooltip rendering view.</p>
 *
 * @author Ocelot
 * @see Screen
 * @since 2.0.0
 */
@OnlyIn(Dist.CLIENT)
public interface TooltipRenderer
{
    /**
     * Renders the tooltip for the specified {@link ItemStack}.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param stack The stack to render the tooltip of
     * @param posX  The x position to render the tooltip at
     * @param posY  The t position to render the tooltip at
     */
    default void renderTooltip(MatrixStack matrixStack, ItemStack stack, int posX, int posY)
    {
        renderTooltip(matrixStack, stack, posX, posY, Minecraft.getInstance().fontRenderer);
    }

    /**
     * Renders the tooltip for the specified {@link ItemStack}.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param stack        The stack to render the tooltip of
     * @param posX         The x position to render the tooltip at
     * @param posY         The t position to render the tooltip at
     * @param fontRenderer The font to use when rendering the text if the stack provides no custom font renderer
     */
    default void renderTooltip(MatrixStack matrixStack, ItemStack stack, int posX, int posY, FontRenderer fontRenderer)
    {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(stack);
        this.renderTooltip(matrixStack, this.getTooltipFromItem(stack), posX, posY, (font == null ? fontRenderer : font));
        net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
    }

    /**
     * Renders a tooltip using the specified string.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param tooltip The string to render on the tooltip
     * @param posX    The x position to render the tooltip at
     * @param posY    The y position to render the tooltip at
     */
    void renderTooltip(MatrixStack matrixStack, ITextProperties tooltip, int posX, int posY);

    /**
     * Renders a tooltip using the specified string.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param tooltip      The string to render on the tooltip
     * @param posX         The x position to render the tooltip at
     * @param posY         The y position to render the tooltip at
     * @param fontRenderer The font to use when rendering the text
     */
    default void renderTooltip(MatrixStack matrixStack, ITextProperties tooltip, int posX, int posY, FontRenderer fontRenderer)
    {
        this.renderTooltip(matrixStack, Collections.singletonList(tooltip), posX, posY, fontRenderer);
    }

    /**
     * Renders a tooltip using the specified strings.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param tooltip The strings to render on the tooltip
     * @param posX    The x position to render the tooltip at
     * @param posY    The y position to render the tooltip at
     */
    void renderTooltip(MatrixStack matrixStack, List<? extends ITextProperties> tooltip, int posX, int posY);

    /**
     * Renders a tooltip using the specified strings.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param tooltip      The strings to render on the tooltip
     * @param posX         The x position to render the tooltip at
     * @param posY         The y position to render the tooltip at
     * @param fontRenderer The font to use when rendering the text
     */
    void renderTooltip(MatrixStack matrixStack, List<? extends ITextProperties> tooltip, int posX, int posY, FontRenderer fontRenderer);

    /**
     * Renders the tooltip for the specified text component.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param style The style of the component to render the tooltip for
     * @param posX          The x position to render the tooltip at
     * @param posY          The y position to render the tooltip at
     */
    void renderComponentHoverEffect(MatrixStack matrixStack, @Nullable Style style, int posX, int posY);

    /**
     * Collects the tooltip information from the specified {@link ItemStack}.
     *
     * @param stack The stack to get the tooltip information from
     * @return The lines of tooltip information
     */
    List<ITextComponent> getTooltipFromItem(ItemStack stack);
}
