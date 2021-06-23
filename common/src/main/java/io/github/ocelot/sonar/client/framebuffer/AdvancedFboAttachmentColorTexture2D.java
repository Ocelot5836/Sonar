package io.github.ocelot.sonar.client.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

/**
 * <p>An attachment for an {@link AdvancedFbo} that represents a color texture buffer.</p>
 *
 * @author Ocelot
 * @since 2.4.0
 */
public class AdvancedFboAttachmentColorTexture2D extends AbstractTexture implements AdvancedFboTextureAttachment
{
    private final int width;
    private final int height;
    private final int mipmapLevels;

    public AdvancedFboAttachmentColorTexture2D(int width, int height, int mipmapLevels)
    {
        this.width = width;
        this.height = height;
        this.mipmapLevels = mipmapLevels;
    }

    @Override
    public void create()
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() ->
            {
                this.setFilter(false, this.mipmapLevels > 1);
                TextureUtil.prepareImage(this.getId(), this.mipmapLevels, this.width, this.height);
            });
        }
        else
        {
            this.setFilter(false, this.mipmapLevels > 1);
            TextureUtil.prepareImage(this.getId(), this.mipmapLevels, this.width, this.height);
        }
    }

    @Override
    public void attach(int target, int attachment, int level)
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() -> glFramebufferTexture2D(target, GL_COLOR_ATTACHMENT0 + attachment, GL_TEXTURE_2D, this.getId(), level));
        }
        else
        {
            glFramebufferTexture2D(target, GL_COLOR_ATTACHMENT0 + attachment, GL_TEXTURE_2D, this.getId(), level);
        }
    }

    @Override
    public int getMipmapLevels()
    {
        return mipmapLevels;
    }

    @Override
    public AdvancedFboAttachmentColorTexture2D createCopy()
    {
        return new AdvancedFboAttachmentColorTexture2D(this.width, this.height, this.mipmapLevels);
    }

    @Override
    public void bindAttachment()
    {
        this.bind();
    }

    @Override
    public void unbindAttachment()
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() -> GlStateManager._bindTexture(0));
        }
        else
        {
            GlStateManager._bindTexture(0);
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
        return 1;
    }

    @Override
    public boolean canSample()
    {
        return true;
    }

    @Override
    public void free()
    {
        this.releaseId();
    }

    @Override
    public void load(ResourceManager manager)
    {
        this.create();
    }
}
