package io.github.ocelot.sonar.client.util.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class OnlineImageCacheImpl
{
    public static void registerClientTick(Runnable task)
    {
        ClientTickEvents.END_CLIENT_TICK.register(client -> task.run());
    }
}
