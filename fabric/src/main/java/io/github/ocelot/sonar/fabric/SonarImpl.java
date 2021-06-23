package io.github.ocelot.sonar.fabric;

import io.github.ocelot.sonar.core.SonarDevelopmentPack;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SonarImpl
{
    private static void initClient(Runnable clientInit)
    {
        SonarDevelopmentPack.init();
        clientInit.run();
    }

    private static void initCommon(Runnable commonInit)
    {
        commonInit.run();
    }

    private static void setupClient(Runnable clientSetup)
    {
        clientSetup.run();
    }

    private static void setupCommon(Runnable commonSetup)
    {
        commonSetup.run();
    }

    @SuppressWarnings("deprecation")
    public static BlockableEventLoop<?> getSidedExecutor(boolean client)
    {
        FabricLoader loader = FabricLoader.getInstance();
        return loader.getEnvironmentType() == EnvType.CLIENT ? Minecraft.getInstance() : (BlockableEventLoop<?>) loader.getGameInstance();
    }
}
