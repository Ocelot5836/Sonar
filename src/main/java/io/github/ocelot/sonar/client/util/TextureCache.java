package io.github.ocelot.sonar.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.sonar.common.util.OnlineRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * <p>Requests and caches online images based on the implementation.</p>
 *
 * @author Ocelot
 * @since 6.1.0
 */
@OnlyIn(Dist.CLIENT)
public interface TextureCache
{
    /**
     * A cache instance that does not cache textures and downloads them each time.
     */
    TextureCache NONE = url ->
    {
        Logger logger = LogManager.getLogger();
        logger.info("Requesting image from '" + url + "'");
        return OnlineRequest.request(url, Util.getServerExecutor()).thenApplyAsync(stream ->
        {
            try
            {
                NativeImage image = NativeImage.read(stream);
                ResourceLocation location = new ResourceLocation(DigestUtils.md5Hex(url));
                Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                return location;
            }
            catch (Exception e)
            {
                logger.error("Failed to load image from '" + url + "'", e);
            }
            return MissingTextureSprite.getLocation();
        }, task -> RenderSystem.recordRenderCall(task::run));
    };

    /**
     * Fetches an image from the specified url and caches the result as long as this cache specifies.
     *
     * @param url The url to get the image from
     * @return The location of the texture downloaded or <code>null</code> if it is currently being processed
     * @deprecated Use {@link #requestTexture(String)} instead. TODO remove in 7.0.0
     */
    @Nullable
    default ResourceLocation getTextureLocation(String url)
    {
        return this.requestTexture(url).getNow(null);
    }

    /**
     * Fetches an image from the specified url and caches the result as long as this cache specifies.
     *
     * @param url The url to get the image from
     * @return The location of the texture downloaded
     */
    CompletableFuture<ResourceLocation> requestTexture(String url);
}
