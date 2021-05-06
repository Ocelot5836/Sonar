package io.github.ocelot.sonar.client.util;

import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
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
public class SonarSpriteUploader extends SpriteUploader
{
    private final Set<ResourceLocation> registeredSprites;
    private final Set<Supplier<Collection<ResourceLocation>>> registeredSpriteSuppliers;

    public SonarSpriteUploader(TextureManager textureManager, ResourceLocation textureLocation, String prefix)
    {
        super(textureManager, textureLocation, prefix);
        this.registeredSprites = new HashSet<>();
        this.registeredSpriteSuppliers = new HashSet<>();
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

    @Override
    protected Stream<ResourceLocation> getResourceLocations()
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
    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return super.getSprite(location);
    }
}
