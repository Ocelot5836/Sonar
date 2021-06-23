package io.github.ocelot.sonar.core.fabric;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.fabric.SonarModContextImpl;
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
        ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(Sonar.DOMAIN, Sonar.DOMAIN), ((SonarModContextImpl) Sonar.context()).getModContainer(), ResourcePackActivationType.ALWAYS_ENABLED);
    }
}
