package io.github.ocelot.client;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.ocelot.common.OnlineRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>Loads and caches images from the internet. The cache can be given an expiration time which allows for images to be redownloaded when required.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class OnlineImageCache
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Path cacheFolder;
    private final Path cacheFile;
    private final Map<String, ResourceLocation> cache;
    private final Set<String> errored;
    private final Set<String> requested;
    private JsonObject cacheFileData;

    public OnlineImageCache(String cacheFolderName)
    {
        this.cacheFolder = Minecraft.getInstance().gameDir.toPath().resolve(cacheFolderName);
        this.cacheFile = this.cacheFolder.resolve("cache.json");
        this.cache = new HashMap<>();
        this.errored = new HashSet<>();
        this.requested = new HashSet<>();

        try (InputStreamReader is = new InputStreamReader(new FileInputStream(this.cacheFile.toFile())))
        {
            this.cacheFileData = new JsonParser().parse(is).getAsJsonObject();
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to load cache from '" + this.cacheFile + "'", e);
            this.cacheFileData = new JsonObject();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    private boolean hasExpired(String hash)
    {
        return !this.cacheFileData.has(hash) || (this.cacheFileData.get(hash).getAsLong() != -1 && System.currentTimeMillis() - this.cacheFileData.get(hash).getAsLong() > 0);
    }

    private void deleteCache(String hash) throws IOException
    {
        Path imageFile = this.cacheFolder.resolve(hash);
        if (!Files.exists(imageFile))
            return;

        LOGGER.debug("Deleting '" + hash + "' from cache.");
        this.cacheFileData.remove(hash);
        Files.delete(imageFile);
    }

    @Nullable
    private NativeImage loadCache(String hash) throws IOException
    {
        if (!Files.exists(this.cacheFolder))
            return null;

        Path imageFile = this.cacheFolder.resolve(hash);
        if (!Files.exists(imageFile))
            return null;

        if (this.hasExpired(hash))
            return null;

        LOGGER.debug("Reading '" + hash + "' from cache.");
        try (FileInputStream is = new FileInputStream(imageFile.toFile()))
        {
            return NativeImage.read(is);
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to load image with hash '" + hash + "' from cache.", e);
            this.deleteCache(hash);
        }
        return null;
    }

    private void writeCache(String hash, NativeImage image, long expirationDate) throws IOException
    {
        LOGGER.debug("Writing '" + hash + "' to cache.");

        if (!Files.exists(this.cacheFolder))
            Files.createDirectories(this.cacheFolder);
        if (!Files.exists(this.cacheFile))
            Files.createFile(this.cacheFile);

        this.cacheFileData.addProperty(hash, expirationDate);
        try (FileOutputStream os = new FileOutputStream(this.cacheFile.toFile()))
        {
            IOUtils.write(this.cacheFileData.toString(), os, Charsets.UTF_8);
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to write cache to file.", e);
        }

        image.write(this.cacheFolder.resolve(hash));
    }

    /**
     * Fetches an image from the specified url and caches the result forever.
     *
     * @param url The url to get the image from
     * @return The location of the texture downloaded or null if it is currently being processed
     */
    @Nullable
    public ResourceLocation getTextureLocation(String url)
    {
        return this.getTextureLocation(url, TimeUnit.MILLISECONDS, -1);
    }

    /**
     * Fetches an image from the specified url and caches the result for the specified amount of time before redownloading.
     *
     * @param url            The url to get the image from
     * @param timeUnit       The unit of time to use
     * @param expirationTime The amount of time to keep the image around for or -1 for infinite
     * @return The location of the texture downloaded or null if it is currently being processed
     */
    @Nullable
    public ResourceLocation getTextureLocation(String url, TimeUnit timeUnit, long expirationTime)
    {
        String hash = DigestUtils.md5Hex(url);
        if (this.errored.contains(hash))
            return MissingTextureSprite.getLocation();

        ResourceLocation location = this.cache.computeIfAbsent(hash, ResourceLocation::new);
        if (Minecraft.getInstance().getTextureManager().getTexture(location) != null)
            return location;

        if (this.requested.contains(hash))
            return null;

        try
        {
            NativeImage cachedImage = this.loadCache(hash);
            if (cachedImage != null)
            {
                Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(cachedImage));
                return location;
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to load image with hash '" + hash + "' from cache.", e);
        }

        LOGGER.debug("Requesting image from '" + hash + "'");
        this.requested.add(hash);
        OnlineRequest.make(url, result ->
        {
            try
            {
                NativeImage image = NativeImage.read(result);
                this.writeCache(hash, image, expirationTime == -1 ? -1 : System.currentTimeMillis() + timeUnit.toMillis(expirationTime));
                Minecraft.getInstance().execute(() ->
                {
                    Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                    this.requested.remove(hash);
                });
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to load online texture from '" + url + "'. Using missing texture sprite.", e);
                Minecraft.getInstance().execute(() ->
                {
                    this.errored.add(hash);
                    this.requested.remove(hash);
                });
            }
        }, e ->
        {
            LOGGER.error("Failed to load online texture from '" + url + "'. Using missing texture sprite.", e);
            Minecraft.getInstance().execute(() ->
            {
                this.errored.add(hash);
                this.requested.remove(hash);
            });
        });
        return null;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEvent(TickEvent.ClientTickEvent event)
    {
        this.cache.forEach((hash, location) ->
        {
            if (this.hasExpired(hash))
            {
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().deleteTexture(location));
            }
        });
        this.cache.entrySet().removeIf(entry -> Minecraft.getInstance().getTextureManager().getTexture(entry.getValue()) == null);
    }
}
