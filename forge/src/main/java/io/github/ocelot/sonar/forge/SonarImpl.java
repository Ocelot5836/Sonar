package io.github.ocelot.sonar.forge;

import io.github.ocelot.sonar.SonarModContext;
import io.github.ocelot.sonar.core.SonarDevelopmentPack;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SonarImpl
{
    public static void initClient(SonarModContext context, Runnable clientInit)
    {
        SonarDevelopmentPack.init();
        clientInit.run();
    }

    public static void initCommon(SonarModContext context, Runnable commonInit)
    {
        commonInit.run();
    }

    public static void setupClient(SonarModContext context, Runnable clientSetup)
    {
        ((SonarModContextImpl) context).getModBus().<FMLClientSetupEvent>addListener(event -> clientSetup.run());
    }

    public static void setupCommon(SonarModContext context, Runnable commonSetup)
    {
        ((SonarModContextImpl) context).getModBus().<FMLCommonSetupEvent>addListener(event -> commonSetup.run());
    }
}
