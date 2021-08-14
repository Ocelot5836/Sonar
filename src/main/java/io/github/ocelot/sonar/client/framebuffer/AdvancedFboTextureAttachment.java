package io.github.ocelot.sonar.client.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.Validate;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

/**
 * <p>An attachment for an {@link AdvancedFbo} that represents a color texture buffer.</p>
 *
 * @author Ocelot
 * @since 2.4.0
 */
public class AdvancedFboTextureAttachment extends AbstractTexture implements AdvancedFboAttachment
{
    private final int attachmentType;
    private final int width;
    private final int height;
    private final int mipmapLevels;

    public AdvancedFboTextureAttachment(int attachmentType, int width, int height, int mipmapLevels)
    {
        this.attachmentType = attachmentType;
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
                this.setBlurMipmap(false, this.mipmapLevels > 1);
                TextureUtil.m_85287_(this.getId(), this.mipmapLevels, this.width, this.height);
            });
        }
        else
        {
            this.setBlurMipmap(false, this.mipmapLevels > 1);
            TextureUtil.m_85287_(this.getId(), this.mipmapLevels, this.width, this.height);
        }
    }

    @Override
    public void attach(int target, int attachment)
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() ->  this._attach(target, attachment));
        }
        else
        {
            this._attach(target, attachment);
        }
    }

    private void _attach(int target, int attachment){
        Validate.isTrue(this.attachmentType != GL_DEPTH_ATTACHMENT || attachment == 0, "Only one depth buffer attachment is supported.");
        for (int level = 0; level <= this.getMipmapLevels(); level++){
            glFramebufferTexture2D(target, this.attachmentType + attachment, GL_TEXTURE_2D, this.getId(), level);
        }
    }

    public int getMipmapLevels()
    {
        return mipmapLevels;
    }

    @Override
    public AdvancedFboTextureAttachment createCopy()
    {
        return new AdvancedFboTextureAttachment(this.attachmentType, this.width, this.height, this.mipmapLevels);
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
