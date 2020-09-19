package io.github.ocelot.sonar.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
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
    private ShapeRenderer()
    {
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     * @param sprite      The sprite to render to the screen
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(matrixStack, x, y, 0, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param u           The x position on the texture to start
     * @param v           The y position on the texture to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, float u, float v, double width, double height)
    {
        drawRectWithTexture(matrixStack, x, y, 0, u, v, width, height, (float) width, (float) height, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param matrixStack   The stack of transformations to move elements
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
        drawRectWithTexture(matrixStack, x, y, 0, u, v, width, height, textureWidth, textureHeight, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param matrixStack   The stack of transformations to move elements
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
        drawRectWithTexture(matrixStack, x, y, 0, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param z           The z position to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     * @param sprite      The sprite to render to the screen
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(matrixStack, x, y, z, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param z           The z position to start
     * @param u           The x position on the texture to start
     * @param v           The y position on the texture to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height)
    {
        drawRectWithTexture(matrixStack, x, y, z, u, v, width, height, (float) width, (float) height, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
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
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height, float textureWidth, float textureHeight)
    {
        drawRectWithTexture(matrixStack, x, y, z, u, v, width, height, textureWidth, textureHeight, 256f, 256f);
    }

    /**
     * Draws a quad onto the screen with the specified parameters.
     *
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
     */
    public static void drawRectWithTexture(MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height, float textureWidth, float textureHeight, float sourceWidth, float sourceHeight)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        begin();
        drawRectWithTexture(buffer, matrixStack, x, y, z, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
        tessellator.draw();
    }

    /**
     * Begins the rendering of a chain of quads.
     *
     * @return The buffer to render into
     */
    public static IVertexBuilder begin()
    {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        return buffer;
    }

    /**
     * Ends the rendering of a chain of quads.
     */
    public static void end()
    {
        Tessellator.getInstance().draw();
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer      The buffer being rendered into
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     * @param sprite      The sprite to render to the screen
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, 0, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer      The buffer being rendered into
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param u           The x position on the texture to start
     * @param v           The y position on the texture to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, float u, float v, double width, double height)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, 0, u, v, width, height, (float) width, (float) height, 256f, 256f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param matrixStack   The stack of transformations to move elements
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
        drawRectWithTexture(buffer, matrixStack, x, y, 0, u, v, width, height, textureWidth, textureHeight, 256f, 256f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer        The buffer being rendered into
     * @param matrixStack   The stack of transformations to move elements
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
        drawRectWithTexture(buffer, matrixStack, x, y, 0, u, v, width, height, textureWidth, textureHeight, sourceWidth, sourceHeight);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer      The buffer being rendered into
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param z           The z position to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     * @param sprite      The sprite to render to the screen
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double z, double width, double height, TextureAtlasSprite sprite)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, z, sprite.getMinU(), sprite.getMinV(), width, height, sprite.getMaxU() - sprite.getMinU(), sprite.getMaxV() - sprite.getMinV(), 1f, 1f);
    }

    /**
     * Draws a quad into the specified buffer for chain rendering.
     *
     * @param buffer      The buffer being rendered into
     * @param matrixStack The stack of transformations to move elements
     * @param x           The x position to start
     * @param y           The y position to start
     * @param z           The z position to start
     * @param u           The x position on the texture to start
     * @param v           The y position on the texture to start
     * @param width       The x size of the quad
     * @param height      The y size of the quad
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height)
    {
        drawRectWithTexture(buffer, matrixStack, x, y, z, u, v, width, height, (float) width, (float) height, 256f, 256f);
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
     */
    public static void drawRectWithTexture(IVertexBuilder buffer, MatrixStack matrixStack, double x, double y, double z, float u, float v, double width, double height, float textureWidth, float textureHeight, float sourceWidth, float sourceHeight)
    {
        float scaleWidth = 1f / sourceWidth;
        float scaleHeight = 1f / sourceHeight;
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        buffer.pos(matrix4f, (float) x, (float) (y + height), (float) z).tex(u * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) (y + height), (float) z).tex((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) y, (float) z).tex((u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
        buffer.pos(matrix4f, (float) x, (float) y, (float) z).tex(u * scaleWidth, v * scaleHeight).endVertex();
    }
}
