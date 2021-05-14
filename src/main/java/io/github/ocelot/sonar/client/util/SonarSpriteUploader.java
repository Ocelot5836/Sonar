package io.github.ocelot.sonar.client.util;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>Registers sprites into their own texture atlas.</p>
 *
 * @author Ocelot
 * @since 6.1.0
 */
@OnlyIn(Dist.CLIENT)
public class SonarSpriteUploader extends ReloadListener<AtlasTexture.SheetData> implements AutoCloseable
{
    private final AtlasTexture textureAtlas;
    private final String prefix;
    private final Set<ResourceLocation> registeredSprites;
    private final Set<Supplier<Collection<ResourceLocation>>> registeredSpriteSuppliers;
    private int mipmapLevels;

    public SonarSpriteUploader(TextureManager textureManager, ResourceLocation textureLocation, String prefix)
    {
        this.prefix = prefix;
        this.textureAtlas = new AtlasTexture(textureLocation);
        this.registeredSprites = new HashSet<>();
        this.registeredSpriteSuppliers = new HashSet<>();
        this.mipmapLevels = 0;
        textureManager.register(this.textureAtlas.location(), this.textureAtlas);
    }

    /**
     * Registers the specified sprite to be added into the atlas.
     *
     * @param location The location of the sprite to add
     */
    public void registerSprite(ResourceLocation location)
    {
        this.registeredSprites.add(location);
    }

    /**
     * Registers the specified sprite supplier that will be resolved each query
     *
     * @param supplier The supplier for a collection of sprites
     */
    public void registerSpriteSupplier(Supplier<Collection<ResourceLocation>> supplier)
    {
        this.registeredSpriteSuppliers.add(supplier);
    }

    private Stream<ResourceLocation> getResourceLocations()
    {
        Set<ResourceLocation> locations = new HashSet<>(this.registeredSprites);
        this.registeredSpriteSuppliers.stream().map(Supplier::get).forEach(locations::addAll);
        return Collections.unmodifiableSet(locations).stream();
    }

    /**
     * Retrieves a sprite by the specified name.
     *
     * @param location The location of the sprite to fetch
     * @return The sprite with that id or the missing sprite
     */
    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return this.textureAtlas.getSprite(this.resolveLocation(location));
    }

    private ResourceLocation resolveLocation(ResourceLocation location)
    {
        return new ResourceLocation(location.getNamespace(), this.prefix + "/" + location.getPath());
    }

    @Override
    protected AtlasTexture.SheetData prepare(IResourceManager resourceManager, IProfiler profiler)
    {
        profiler.startTick();
        profiler.push("stitching");
        AtlasTexture.SheetData atlastexture$sheetdata = this.textureAtlas.prepareToStitch(resourceManager, this.getResourceLocations().map(this::resolveLocation), profiler, this.mipmapLevels);
        profiler.pop();
        profiler.endTick();
        return atlastexture$sheetdata;
    }

    @Override
    protected void apply(AtlasTexture.SheetData object, IResourceManager resourceManager, IProfiler profiler)
    {
        profiler.startTick();
        profiler.push("upload");
        this.textureAtlas.reload(object);
        profiler.pop();
        profiler.endTick();
    }

    @Override
    public void close()
    {
        this.textureAtlas.clearTextureData();
    }

    /**
     * @return The levels of mipmapping
     */
    public int getMipmapLevels()
    {
        return mipmapLevels;
    }

    /**
     * Sets the amount of mipmaps to have.
     *
     * @param mipmapLevels The levels of mipmap
     */
    public void setMipmapLevels(int mipmapLevels)
    {
        this.mipmapLevels = Math.max(0, mipmapLevels);
    }
}
