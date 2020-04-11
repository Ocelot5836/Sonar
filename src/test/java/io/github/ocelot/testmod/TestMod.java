package io.github.ocelot.testmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TestMod.MOD_ID)
public class TestMod
{
    public static final String MOD_ID = "examplemod";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public TestMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
    }

    private void init(FMLCommonSetupEvent event)
    {
    }

    private void initClient(FMLClientSetupEvent event)
    {
    }
}
