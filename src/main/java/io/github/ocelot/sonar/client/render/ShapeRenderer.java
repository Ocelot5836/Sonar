package io.github.ocelot.sonar.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11C.GL_QUADS;

/**
 * <p>Renders {@link GL11C#GL_QUADS} to the screen using enhanced precision and {@link BufferBuilder}.</p>
 * <p>To use chain rendering, use {@link #begin()} to start rendering and {@link #end()} to complete a batch.</p>
 *
 * @author Ocelot
 * @since 2.0.0
 */
@OnlyIn(Dist.CLIENT)
public final class ShapeRenderer
{
    private static double zLevel = 0.0;
    private static float red = 1.0F;
    private static float green = 1.0F;
    private static float blue = 1.0F;
    private static float alpha = 1.0F;

    private ShapeRenderer()
    {
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(matrixStack, x, y, zLevel, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, float u, float v, double width, double height)
    {
        drawRectWithTexture(matrixStack, x, y, zLevel, u, v, width, height, (float) width, (float) height, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x             The x position to start
     * @param y             The y position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, float u, float v, double width, double height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(matrixStack, x, y, zLevel, u, v, width, height, textureWidth, textureHeight, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x             The x position to start
     * @param y             The y position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @param sourceWidth   The width of the texture source
     * @param sourceHeight  The height of the texture source
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, float u, float v, double width, double height, float textureWidth, float textureHeight, float sourceWidth, float sourceHeight)
    {
        drawRectWithTexture(matrixStack, x, y, zLevel, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(matrixStack, x, y, z, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height)
    {
        drawRectWithTexture(matrixStack, x, y, z, u, v, width, height, (float) width, (float) height, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x             The x position to start
     * @param y             The y position to start
     * @param z             The z position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(matrixStack, x, y, z, u, v, width, height, textureWidth, textureHeight, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param x             The x position to start
     * @param y             The y position to start
     * @param z             The z position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @param sourceWidth   The width of the texture source
     * @param sourceHeight  The height of the texture source
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height, float textureWidth, float textureHeight, float sourceWidth, float sourceHeight)
    {
        drawRectWithTexture(begin(), matrixStack, x, y, z, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
        end();
    }

    /**
     * Begins the rendering of a chain of quads.
     *
     * @return The buffer to render into
     */
    public static IVertexBuilder begin()
    {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        return buffer;
    }

    /**
     * Ends the rendering of a chain of quads.
     */
    public static void end()
    {
        Tessellator.getInstance().draw();
        zLevel = 0;
        resetColor();
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, zLevel, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, float u, float v, double width, double height)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, zLevel, u, v, width, height, (float) width, (float) height, 256f, 256f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param x             The x position to start
     * @param y             The y position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, float u, float v, double width, double height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, zLevel, u, v, width, height, textureWidth, textureHeight, 256f, 256f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param x             The x position to start
     * @param y             The y position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @param sourceWidth   The width of the texture source
     * @param sourceHeight  The height of the texture source
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, float u, float v, double width, double height, float textureWidth, float textureHeight, float sourceWidth, float sourceHeight)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, zLevel, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @param sprite The sprite to render to the screen
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double z, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, z, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer The buffer being rendered into
     * @param x      The x position to start
     * @param y      The y position to start
     * @param z      The z position to start
     * @param u      The x position on the texture to start
     * @param v      The y position on the texture to start
     * @param width  The x size of the quad
     * @param height The y size of the quad
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, z, u, v, width, height, (float) width, (float) height, 256f, 256f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param x             The x position to start
     * @param y             The y position to start
     * @param z             The z position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, z, u, v, width, height, textureWidth, textureHeight, 256, 256);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param matrixStack   The stack of transformations to move elements
     * @param x             The x position to start
     * @param y             The y position to start
     * @param z             The z position to start
     * @param u             The x position on the texture to start
     * @param v             The y position on the texture to start
     * @param width         The x size of the quad
     * @param height        The y size of the quad
     * @param textureWidth  The x size of the selection area on the texture
     * @param textureHeight The y size on the selection area on the texture
     * @param sourceWidth   The width of the texture source
     * @param sourceHeight  The height of the texture source
     * @deprecated Use {@link #setZLevel(double)} to change z values. TODO remove in 6.0.0
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height, float textureWidth, float textureHeight, float sourceWidth, float sourceHeight)
    {
        float scaleWidth = 1f / sourceWidth;
        float scaleHeight = 1f / sourceHeight;
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        buffer.pos(matrix4f, (float) x, (float) (y + height), (float) z).color(red, green, blue, alpha).tex(u * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) (y + height), (float) z).color(red, green, blue, alpha).tex((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) y, (float) z).color(red, green, blue, alpha).tex((u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(matrix4f, (float) x, (float) y, (float) z).color(red, green, blue, alpha).tex(u * scaleWidth, v * scaleHeight).endVertex();
    }

    /**
     * Sets the color back to white.
     */
    public static void resetColor()
    {
        ShapeRenderer.red = 1.0F;
        ShapeRenderer.green = 1.0F;
        ShapeRenderer.blue = 1.0F;
        ShapeRenderer.alpha = 1.0F;
    }

    /**
     * Sets the z parameter for rendering.
     *
     * @param zLevel The new z value
     */
    public static void setZLevel(double zLevel)
    {
        ShapeRenderer.zLevel = zLevel;
    }

    /**
     * Sets the color values to the provided red, green, and blue. Should be from <code>0.0F</code> to <code>1.0F</code>.
     *
     * @param red   The new red value
     * @param green The new green value
     * @param blue  The new blue value
     * @param alpha The new alpha value
     */
    public static void setColor(float red, float green, float blue, float alpha)
    {
        ShapeRenderer.red = red;
        ShapeRenderer.green = green;
        ShapeRenderer.blue = blue;
        ShapeRenderer.alpha = alpha;
    }

    /**
     * Sets the color values to the provided color integer.
     *
     * @param color The four color values in the order of <code>0xRRGGBBAA</code>
     */
    public static void setColor(int color)
    {
        ShapeRenderer.red = ((color >> 16) & 0xff) / 255f;
        ShapeRenderer.green = ((color >> 8) & 0xff) / 255f;
        ShapeRenderer.blue = (color & 0xff) / 255f;
        ShapeRenderer.alpha = ((color >> 24) & 0xff) / 255f;
    }
}
