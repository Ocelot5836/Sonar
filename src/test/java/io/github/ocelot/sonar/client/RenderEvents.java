package io.github.ocelot.sonar.client;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.client.cache.OnlineImageCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT)
public class RenderEvents
{
    private static final OnlineImageCache CACHE = new OnlineImageCache(TestMod.MOD_ID + "-texture-cache", 10, TimeUnit.SECONDS);

    @SubscribeEvent
    public static void onEvent(RenderGameOverlayEvent event)
    {
        System.out.println(CACHE.getTextureLocation("https://www.paypalobjects.com/HomeScreen/direct-deposit.png"));
    }
}
