package io.github.ocelot.sonar.client;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11C;

import java.util.EmptyStackException;
import java.util.Stack;

import static org.lwjgl.opengl.GL11C.*;

/**
 * <p>Handles scissoring parts of </p>
 *
 * @author Ocelot
 * @see GL11C <a target="_blank" href="http://docs.gl/gl4/glScissor">OpenGL Scissor Test Reference Page</a>
 * @since 2.0.0
 */
@OnlyIn(Dist.CLIENT)
public final class ScissorHelper
{
    private static final Stack<Entry> stack = new Stack<>();
    private static boolean scissor = glGetBoolean(GL_SCISSOR_TEST);

    /**
     * Specifies the height of the entire non-scaled FBO. This should only be used if rendering into a custom frame buffer and reset back to 0 when rendering with the minecraft frame buffer.
     */
    public static int framebufferHeight = 0;
    /**
     * Specifies the scale factor {@link #framebufferHeight} is modified by. Usually GUI scale setting in Minecraft. This should only be used if rendering in a custom frame buffer. This should only be used if rendering in a custom frame buffer and reset back to 0 when rendering with the minecraft frame buffer.
     */
    public static double framebufferScale = 0;

    private ScissorHelper()
    {
    }

    private static void applyScissor()
    {
        if (!stack.isEmpty())
        {
            Entry entry = stack.peek();
            MainWindow window = Minecraft.getInstance().getMainWindow();
            double scale = framebufferScale == 0 ? window.getGuiScaleFactor() : framebufferScale;
            int frameHeight = framebufferHeight == 0 ? window.getFramebufferHeight() : framebufferHeight;
            enableScissorInternal();
            glScissor((int) (entry.getX() * scale), (int) (frameHeight - (entry.getY() + entry.getHeight()) * scale), (int) Math.max(0, entry.getWidth() * scale), (int) Math.max(0, entry.getHeight() * scale));
        }
        else
        {
            disableScissorInternal();
        }
    }

    private static void enableScissorInternal()
    {
        if (!scissor)
        {
            glEnable(GL_SCISSOR_TEST);
            scissor = true;
        }
    }

    private static void disableScissorInternal()
    {
        if (scissor)
        {
            glDisable(GL_SCISSOR_TEST);
            scissor = false;
        }
    }

    /**
     * Pushes a new scissor test onto the stack. Can be reverted to the previous state by calling {@link #pop()}.
     *
     * @param x      The x position of the rectangle
     * @param y      The y position of the rectangle
     * @param width  The x size of the rectangle
     * @param height The y size of the rectangle
     * @throws IllegalArgumentException If width or height are negative
     */
    public static void push(float x, float y, float width, float height)
    {
        Validate.isTrue(width >= 0, "Scissor width cannot be negative");
        Validate.isTrue(height >= 0, "Scissor height cannot be negative");

        if (!stack.isEmpty())
        {
            Entry parent = stack.peek();

            if (x < parent.getX())
            {
                x = parent.getX();
                width -= parent.getX() - x;
            }
            if (y < parent.getY())
            {
                y = parent.getY();
                height -= parent.getY() - y;
            }

            if (x + width > parent.getX() + parent.getWidth())
                width = parent.getX() + parent.getWidth() - x;
            if (y + height > parent.getY() + parent.getHeight())
                height = parent.getY() + parent.getHeight() - y;
        }

        stack.push(new ScissorHelper.Entry(x, y, width, height));
        applyScissor();
    }

    /**
     * Removes the current scissor from the stack and reverts to the previous state specified.
     */
    public static void pop()
    {
        if (!stack.isEmpty())
            stack.pop();
        applyScissor();
    }

    /**
     * Clears the entire scissor stack.
     */
    public static void clear()
    {
        disableScissorInternal();
        stack.clear();
    }

    /**
     * @return The scissor entry currently being used
     * @throws EmptyStackException If the stack is empty. Use {@link #isEmpty()} to verify there is an entry in the stack
     */
    public static Entry getTop()
    {
        return stack.peek();
    }

    /**
     * @return Whether or not there are any scissor entries in the stack
     */
    public static boolean isEmpty()
    {
        return stack.isEmpty();
    }

    /**
     * <p>A single entry in the scissor stack. Specifies the rectangle used for the scissor test.</p>
     *
     * @author Ocelot
     * @since 0.1.0
     */
    public static class Entry
    {
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        private Entry(float x, float y, float width, float height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * @return The x position of the rectangle starting from the left of the screen
         */
        public float getX()
        {
            return x;
        }

        /**
         * @return The y position of the rectangle starting from the top of the screen
         */
        public float getY()
        {
            return y;
        }

        /**
         * @return The x size of the rectangle
         */
        public float getWidth()
        {
            return width;
        }

        /**
         * @return The y size of the rectangle
         */
        public float getHeight()
        {
            return height;
        }
    }
}
