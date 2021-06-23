package io.github.ocelot.sonar.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * <p>Loads and caches game profiles from {@link SkullBlockEntity}.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class SkinHelper
{
    private static final Map<GameProfile, GameProfile> PROFILE_CACHE = new WeakHashMap<>();

    /**
     * Caches the results of {@link SkullBlockEntity#updateGameprofile(GameProfile)}. Use {@link SkinHelper#updateGameProfileAsync(GameProfile)} to fill the profile without blocking the current thread.
     *
     * @param input The input game profile
     * @return The filled game profile with properties
     */
    @Nullable
    public static synchronized GameProfile updateGameProfile(@Nullable GameProfile input)
    {
        if (input == null)
            return null;
        return PROFILE_CACHE.computeIfAbsent(input, SkullBlockEntity::updateGameprofile);
    }

    /**
     * Caches the results of {@link SkullBlockEntity#updateGameprofile(GameProfile)}.
     *
     * @param input The input game profile
     * @return The filled game profile with properties
     */
    public static CompletableFuture<GameProfile> updateGameProfileAsync(@Nullable GameProfile input)
    {
        if (input == null)
            return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> updateGameProfile(input));
    }

    /**
     * Loads the a texture from the specified player profile.
     *
     * @param input    The profile of the player to load textures for
     * @param type     The type of texture to load
     * @param consumer The listener for when the player texture has been loaded and is ready
     */
    @Environment(EnvType.CLIENT)
    public static void loadPlayerTexture(@Nullable GameProfile input, MinecraftProfileTexture.Type type, Consumer<ResourceLocation> consumer)
    {
        CompletableFuture.runAsync(() ->
        {
            if (input == null)
            {
                consumer.accept(DefaultPlayerSkin.getDefaultSkin());
                return;
            }
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().getInsecureSkinInformation(input);
            if (map.containsKey(type))
            {
                RenderSystem.recordRenderCall(() -> consumer.accept(Minecraft.getInstance().getSkinManager().registerTexture(map.get(type), type)));
            }
            else
            {
                consumer.accept(DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(input)));
            }
        }, Util.backgroundExecutor());
    }
}
