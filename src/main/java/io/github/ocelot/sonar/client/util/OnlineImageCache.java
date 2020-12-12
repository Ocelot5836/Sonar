package io.github.ocelot.sonar.client.util;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.common.util.OnlineRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>Loads and caches images from the internet. The cache can be given an expiration time which allows for images to be redownloaded when required.</p>
 * <p>Textures will also be deleted when not looked at for the specified texture cache time which can be disabled by passing <code>-1</code> as the <code>textureCacheTime</code> in the constructors.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
@OnlyIn(Dist.CLIENT)
public class OnlineImageCache
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Path cacheFolder;
    private final Path cacheFile;
    private final Map<String, ResourceLocation> locationCache;
    private final Set<String> errored;
    private final Set<String> requested;
    private final Map<String, Long> textureCache;
    private final long textureCacheTime;
    private JsonObject cacheFileData;

    public OnlineImageCache()
    {
        this(Sonar.DOMAIN, -1, TimeUnit.MILLISECONDS);
    }

    public OnlineImageCache(String domain)
    {
        this(domain, -1, TimeUnit.MILLISECONDS);
    }

    public OnlineImageCache(long textureCacheTime, TimeUnit unit)
    {
        this(Sonar.DOMAIN, textureCacheTime, unit);
    }

    public OnlineImageCache(String domain, long textureCacheTime, TimeUnit unit)
    {
        this.cacheFolder = Minecraft.getInstance().gameDir.toPath().resolve(domain + "-online-image-cache");
        this.cacheFile = this.cacheFolder.resolve("cache.json");
        this.locationCache = new HashMap<>();
        this.errored = new HashSet<>();
        this.requested = new HashSet<>();
        this.textureCache = new HashMap<>();
        this.textureCacheTime = unit.toMillis(textureCacheTime);

        if (Files.exists(this.cacheFile))
        {
            try (InputStreamReader is = new InputStreamReader(new FileInputStream(this.cacheFile.toFile())))
            {
                this.cacheFileData = new JsonParser().parse(is).getAsJsonObject();
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to load cache from '" + this.cacheFile + "'", e);
                this.cacheFileData = new JsonObject();
            }
        }
        else
        {
            this.cacheFileData = new JsonObject();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    private boolean hasTextureExpired(String hash)
    {
        return this.textureCacheTime > 0 && (!this.textureCache.containsKey(hash) || System.currentTimeMillis() - this.textureCache.get(hash) > 0);
    }

    private boolean hasExpired(String hash)
    {
        return !this.cacheFileData.has(hash) || (this.cacheFileData.get(hash).getAsLong() != -1 && Instant.now().toEpochMilli() - this.cacheFileData.get(hash).getAsLong() > 0);
    }

    private synchronized boolean loadCache(String hash, ResourceLocation location)
    {
        if (!Files.exists(this.cacheFolder))
            return false;

        Path imageFile = this.cacheFolder.resolve(hash);
        if (!Files.exists(imageFile))
            return false;

        if (this.hasExpired(hash))
            return false;

        SimpleResource.RESOURCE_IO_EXECUTOR.execute(() ->
        {
            try (FileInputStream is = new FileInputStream(imageFile.toFile()))
            {
                NativeImage image = NativeImage.read(is);
                Minecraft.getInstance().execute(() ->
                {
                    Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                    this.textureCache.put(hash, System.currentTimeMillis() + 30000);
                    this.requested.remove(hash);
                });
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to load image with hash '" + hash + "' from cache. Deleting", e);
                try
                {
                    this.cacheFileData.remove(hash);
                    Files.delete(imageFile);
                }
                catch (IOException e1)
                {
                    LOGGER.error("Failed to delete image with hash '" + hash + "' from cache.", e1);
                }
                Minecraft.getInstance().execute(() -> this.requested.remove(hash));
            }
        });
        return true;
    }

    private synchronized void writeCache(String hash, NativeImage image, long expirationDate) throws IOException
    {
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
        String hash = DigestUtils.md5Hex(url);
        if (this.errored.contains(hash))
        {
            this.textureCache.put(hash, System.currentTimeMillis() + 30000);
            return MissingTextureSprite.getLocation();
        }

        ResourceLocation location = this.locationCache.computeIfAbsent(hash, ResourceLocation::new);
        if (Minecraft.getInstance().getTextureManager().getTexture(location) != null)
        {
            this.textureCache.put(hash, System.currentTimeMillis() + 30000);
            return location;
        }

        if (this.requested.contains(hash))
            return null;

        if (this.loadCache(hash, location))
        {
            this.requested.add(hash);
            return null;
        }

        LOGGER.info("Requesting image from '" + hash + "'");
        this.requested.add(hash);
        OnlineRequest.request(url).thenAcceptAsync(result ->
        {
            try
            {
                NativeImage image = NativeImage.read(result);
                this.writeCache(hash, image, Instant.now().toEpochMilli() + this.textureCacheTime);
                Minecraft.getInstance().execute(() ->
                {
                    Minecraft.getInstance().getTextureManager().loadTexture(location, new DynamicTexture(image));
                    this.textureCache.put(hash, System.currentTimeMillis() + 30000);
                    this.requested.remove(hash);
                });
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to load online texture from '" + url + "'. Using missing texture sprite.", e);
                Minecraft.getInstance().execute(() ->
                {
                    this.errored.add(hash);
                    this.textureCache.put(hash, System.currentTimeMillis() + 30000);
                    this.requested.remove(hash);
                });
            }
        });
        return null;
    }

    @SubscribeEvent
    public void onEvent(TickEvent.ClientTickEvent event)
    {
        this.locationCache.entrySet().removeIf(entry -> Minecraft.getInstance().getTextureManager().getTexture(entry.getValue()) == null);
        this.locationCache.forEach((hash, location) ->
        {
            if (this.hasTextureExpired(hash))
            {
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().deleteTexture(location));
            }
        });
        this.errored.removeIf(this::hasTextureExpired);
    }
}
