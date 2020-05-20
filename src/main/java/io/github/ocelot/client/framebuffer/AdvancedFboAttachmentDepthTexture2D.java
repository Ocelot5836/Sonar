package io.github.ocelot.client.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

/**
 * <p>An attachment for an {@link AdvancedFbo} that represents a depth texture buffer.</p>
 *
 * @author Ocelot
 * @since 2.4.0
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedFboAttachmentDepthTexture2D extends Texture implements AdvancedFboTextureAttachment
{
    private final int width;
    private final int height;
    private final int mipmapLevels;

    public AdvancedFboAttachmentDepthTexture2D(int width, int height, int mipmapLevels)
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
            RenderSystem.recordRenderCall(() -> TextureUtil.prepareImage(this.getGlTextureId(), this.mipmapLevels, this.width, this.height));
        }
        else
        {
            TextureUtil.prepareImage(this.getGlTextureId(), this.mipmapLevels, this.width, this.height);
        }
    }

    @Override
    public void attach(int target, int attachment, int level)
    {
        Validate.isTrue(attachment == 0, "Only one depth buffer attachment is supported.");

        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() -> glFramebufferTexture2D(target, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.getGlTextureId(), level));
        }
        else
        {
            glFramebufferTexture2D(target, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.getGlTextureId(), level);
        }
    }

    @Override
    public int getMipmapLevels()
    {
        return mipmapLevels;
    }

    @Override
    public void bind()
    {
        this.bindTexture();
    }

    @Override
    public void unbind()
    {
        if (!RenderSystem.isOnRenderThreadOrInit())
        {
            RenderSystem.recordRenderCall(() -> GlStateManager.bindTexture(0));
        }
        else
        {
            GlStateManager.bindTexture(0);
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
        this.deleteGlTexture();
    }

    @Override
    public void loadTexture(IResourceManager manager)
    {
        this.create();
    }
}
