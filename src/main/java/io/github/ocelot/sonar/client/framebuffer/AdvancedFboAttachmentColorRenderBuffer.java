package io.github.ocelot.sonar.client.framebuffer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

import static org.lwjgl.opengl.GL30.*;

/**
 * <p>An attachment for an {@link AdvancedFbo} that represents a color render buffer.</p>
 *
 * @author Ocelot
 * @since 2.4.0
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedFboAttachmentColorRenderBuffer implements AdvancedFboAttachment
{
    private int id;
    private final int width;
    private final int height;
    private final int samples;

    public AdvancedFboAttachmentColorRenderBuffer(int width, int height, int samples)
    {
        this.id = -1;
        this.width = width;
        this.height = height;
        Validate.inclusiveBetween(1, glGetInteger(GL_MAX_SAMPLES), samples);
        this.samples = samples;
    }

    private void createRaw()
    {
        this.bindAttachment();
        if (this.samples == 1)
        {
            glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA8, this.width, this.height);
        }
        else
        {
            glRenderbufferStorageMultisample(GL_RENDERBUFFER, this.samples, GL_RGBA8, this.width, this.height);
        }
        this.unbindAttachment();
    }

    private int getId()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (this.id == -1)
        {
            this.id = glGenRenderbuffers();
        }

        return this.id;
    }

    @Override
    public void create()
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(this::createRaw);
        }
        else
        {
            this.createRaw();
        }
    }

    @Override
    public void attach(int target, int attachment)
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() -> glFramebufferRenderbuffer(target, GL_COLOR_ATTACHMENT0 + attachment, GL_RENDERBUFFER, this.getId()));
        }
        else
        {
            glFramebufferRenderbuffer(target, GL_COLOR_ATTACHMENT0 + attachment, GL_RENDERBUFFER, this.getId());
        }
    }

    @Override
    public void bindAttachment()
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() -> glBindRenderbuffer(GL_RENDERBUFFER, this.getId()));
        }
        else
        {
            glBindRenderbuffer(GL_RENDERBUFFER, this.getId());
        }
    }

    @Override
    public void unbindAttachment()
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() -> glBindRenderbuffer(GL_RENDERBUFFER, 0));
        }
        else
        {
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
        }
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getSamples()
    {
        return samples;
    }

    @Override
    public boolean canSample()
    {
        return false;
    }

    @Override
    public AdvancedFboAttachmentColorRenderBuffer createCopy()
    {
        return new AdvancedFboAttachmentColorRenderBuffer(this.width, this.height, this.samples);
    }

    @Override
    public void free()
    {
        if (this.id == -1)
            return;

        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(() ->
            {
                glDeleteRenderbuffers(this.id);
                this.id = -1;
            });
        }
        else
        {
            glDeleteRenderbuffers(this.id);
            this.id = -1;
        }
    }
}
