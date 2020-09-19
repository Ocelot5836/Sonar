package io.github.ocelot.sonar.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * <p>Contains methods for drawing text onto the screen.</p>
 *
 * @author Ocelot
 * @see FontRenderer
 * @since 2.2.0
 */
@OnlyIn(Dist.CLIENT)
public final class FontHelper
{
    /**
     * Draws the specified string of text.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param fontRenderer The font renderer to use
     * @param text         The text to trim
     * @param x            The x position of the text
     * @param y            The y position of the text
     * @param color        The color of the text
     * @param shadow       Whether or not to draw a shadow
     */
    public static void drawString(MatrixStack matrixStack, FontRenderer fontRenderer, String text, float x, float y, int color, boolean shadow)
    {
        if (shadow)
            fontRenderer.drawStringWithShadow(matrixStack, text, x, y, color);
        else
            fontRenderer.drawString(matrixStack, text, x, y, color);
    }

    /**
     * Draws the specified string of text to fit within the specified width.
     *
     * @param matrixStack  The stack of transformations used for positioning
     * @param fontRenderer The font renderer to use
     * @param text         The text to trim
     * @param x            The x position of the text
     * @param y            The y position of the text
     * @param width        The max width the text can be before trimming occurs
     * @param color        The color of the text
     * @param shadow       Whether or not to draw a shadow
     */
    public static void drawStringClipped(MatrixStack matrixStack, FontRenderer fontRenderer, String text, float x, float y, int width, int color, boolean shadow)
    {
        drawString(matrixStack, fontRenderer, clipStringToWidth(fontRenderer, text, width), x, y, color, shadow);
    }

    /**
     * Clips the provided string to fit within the provided width.
     *
     * @param fontRenderer The font renderer to use
     * @param text         The text to trim
     * @param width        The max width the text can be before trimming occurs
     * @return The string clipped to the width
     */
    public static String clipStringToWidth(FontRenderer fontRenderer, String text, int width)
    {
        return fontRenderer.getStringWidth(text) > width ? fontRenderer.func_238412_a_(text, width - fontRenderer.getStringWidth("...")) + "..." : text;
    }

    /**
     * Converts Minecraft world time to a 24 hour format.
     *
     * @param time The current world time
     * @return The formatted time
     */
    public static String timeToString(long time, boolean simple)
    {
        int hours = (int) ((Math.floor(time / 1000.0) + 6) % 24);
        int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
        String value = String.format("%02d:%02d", simple ? hours % 12 : hours, minutes);
        if (simple)
            value += " " + (hours / 12 > 0 ? "PM" : "AM");
        return value;
    }
}
