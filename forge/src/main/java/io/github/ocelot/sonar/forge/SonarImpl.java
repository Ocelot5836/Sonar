package io.github.ocelot.sonar.forge;

import io.github.ocelot.sonar.core.SonarDevelopmentPack;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        FMLJavaModLoadingContext.get().getModEventBus().<FMLClientSetupEvent>addListener(event -> clientSetup.run());
    }

    private static void setupCommon(Runnable commonSetup)
    {
        FMLJavaModLoadingContext.get().getModEventBus().<FMLCommonSetupEvent>addListener(event -> commonSetup.run());
    }

    public static BlockableEventLoop<?> getSidedExecutor(boolean client)
    {
        return LogicalSidedProvider.WORKQUEUE.get(client ? LogicalSide.CLIENT : LogicalSide.SERVER);
    }
}
