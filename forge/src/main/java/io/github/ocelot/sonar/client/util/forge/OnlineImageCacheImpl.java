package io.github.ocelot.sonar.client.util.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class OnlineImageCacheImpl
{
    public static void registerClientTick(Runnable task)
    {
        MinecraftForge.EVENT_BUS.<TickEvent.ClientTickEvent>addListener(event -> task.run());
    }
}
