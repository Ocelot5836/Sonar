package io.github.ocelot.sonar.forge;

import io.github.ocelot.sonar.core.SonarDevelopmentPack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.concurrent.Executor;

public final class SonarImpl
{
    private static Runnable clientSetup;
    private static Runnable commonSetup;

    public static String getActiveNamespace()
    {
        return ModLoadingContext.get().getActiveNamespace();
    }

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
        SonarImpl.clientSetup = clientSetup;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SonarImpl::onClientSetup);
    }

    private static void setupCommon(Runnable commonSetup)
    {
        SonarImpl.commonSetup = commonSetup;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SonarImpl::onCommonSetup);
    }

    public static Executor getSidedExecutor(boolean client)
    {
        return LogicalSidedProvider.WORKQUEUE.get(client ? LogicalSide.CLIENT : LogicalSide.SERVER);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event)
    {
        commonSetup.run();
    }

    private static void onClientSetup(FMLClientSetupEvent event)
    {
        clientSetup.run();
    }
}
