package io.github.ocelot.sonar.core.fabric;

import io.github.ocelot.sonar.Sonar;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SonarDevelopmentPackImpl
{
    public static void init()
    {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() || Minecraft.getInstance() == null)
            return;
        ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(Sonar.DOMAIN, Sonar.DOMAIN), FabricLoader.getInstance().getModContainer(Sonar.getParentModId()).orElseThrow(() -> new IllegalStateException("Could not find mod with id: " + Sonar.getParentModId())), ResourcePackActivationType.ALWAYS_ENABLED);
    }
}
