package io.github.ocelot.sonar.common.util.forge;

import io.github.ocelot.sonar.common.util.Scheduler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class SchedulerImpl
{
    private static final Map<Scheduler, Listener> LISTENERS = new HashMap<>();

    public static void registerServerStoppedHook(Scheduler scheduler, Runnable onServerStopped)
    {
        Listener listener = new Listener(onServerStopped);
        LISTENERS.put(scheduler, listener);
        MinecraftForge.EVENT_BUS.register(listener);
    }

    public static void unregisterServerStoppedHook(Scheduler scheduler)
    {
        Listener listener = LISTENERS.remove(scheduler);
        if (listener != null)
            MinecraftForge.EVENT_BUS.unregister(listener);
    }

    private static class Listener
    {
        private final Runnable onServerStopped;

        private Listener(Runnable onServerStopped)
        {
            this.onServerStopped = onServerStopped;
        }

        @SubscribeEvent
        public void onServerStopped(FMLServerStoppingEvent event)
        {
            this.onServerStopped.run();
        }
    }
}
