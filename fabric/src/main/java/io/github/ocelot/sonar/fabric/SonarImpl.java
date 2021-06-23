package io.github.ocelot.sonar.fabric;

import io.github.ocelot.sonar.SonarModContext;
import io.github.ocelot.sonar.core.SonarDevelopmentPack;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SonarImpl
{
    private static void initClient(SonarModContext context, Runnable clientInit)
    {
        SonarDevelopmentPack.init();
        clientInit.run();
    }

    private static void initCommon(SonarModContext context, Runnable commonInit)
    {
        commonInit.run();
    }

    private static void setupClient(SonarModContext context, Runnable clientSetup)
    {
        clientSetup.run();
    }

    private static void setupCommon(SonarModContext context, Runnable commonSetup)
    {
        commonSetup.run();
    }
}
