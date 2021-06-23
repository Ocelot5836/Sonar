package io.github.ocelot.sonar.fabric;

import io.github.ocelot.sonar.SonarModContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SonarModContextImpl implements SonarModContext
{
    private final ModContainer container;

    private SonarModContextImpl(ModContainer container)
    {
        this.container = container;
    }

    public static SonarModContext get(Object... parameters)
    {
        Validate.isTrue(parameters.length == 1 && parameters[0].getClass() == String.class, "Fabric requires the mod id as the parameter");
        String modId = (String) parameters[0];
        return new SonarModContextImpl(FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalStateException("Unknown mod with id: " + modId)));
    }

    public ModContainer getModContainer()
    {
        return container;
    }

    @Override
    public String getParentModId()
    {
        return this.container.getMetadata().getId();
    }

    @Override
    public BlockableEventLoop<?> getSidedExecutor(boolean client)
    {
        FabricLoader loader = FabricLoader.getInstance();
        return loader.getEnvironmentType() == EnvType.CLIENT ? Minecraft.getInstance() : (BlockableEventLoop<?>) loader.getGameInstance();
    }
}
