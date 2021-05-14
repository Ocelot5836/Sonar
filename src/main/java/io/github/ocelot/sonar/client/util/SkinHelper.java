package io.github.ocelot.sonar.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * <p>Loads and caches game profiles from {@link SkullTileEntity}.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
@OnlyIn(Dist.CLIENT)
public class SkinHelper
{
    private static final Map<GameProfile, GameProfile> PROFILE_CACHE = new WeakHashMap<>();

    /**
     * Caches the results of {@link SkullTileEntity#updateContainingBlockInfo()}.
     *
     * @param input The input game profile
     * @return The filled game profile with properties
     */
    @Nullable
    public static GameProfile updateGameProfile(@Nullable GameProfile input)
    {
        if (input == null)
            return null;
        return PROFILE_CACHE.computeIfAbsent(input, SkullTileEntity::updateGameProfile);
    }

    /**
     * Loads the a texture from the specified player profile.
     *
     * @param input    The profile of the player to load textures for
     * @param type     The type of texture to load
     * @param consumer The listener for when the player texture has been loaded and is ready
     */
    public static void loadPlayerTexture(@Nullable GameProfile input, MinecraftProfileTexture.Type type, Consumer<ResourceLocation> consumer)
    {
        CompletableFuture.runAsync(() ->
        {
            if (input == null)
            {
                consumer.accept(DefaultPlayerSkin.getDefaultSkinLegacy());
                return;
            }
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(input);
            if (map.containsKey(type))
            {
                RenderSystem.recordRenderCall(() -> consumer.accept(Minecraft.getInstance().getSkinManager().loadSkin(map.get(type), type)));
            }
            else
            {
                consumer.accept(DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(input)));
            }
        }, Util.getServerExecutor());
    }
}
