package io.github.ocelot.sonar.common.util.fabric;

import io.github.ocelot.sonar.common.util.Scheduler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class SchedulerImpl
{
    private static final Map<Scheduler, Runnable> LISTENERS = new HashMap<>();
    private static final ServerLifecycleEvents.ServerStopping LISTENER = server -> LISTENERS.values().forEach(Runnable::run);

    static
    {
        ServerLifecycleEvents.SERVER_STOPPING.register(LISTENER);
    }

    public static void registerServerStoppedHook(Scheduler scheduler, Runnable onServerStopped)
    {
        LISTENERS.put(scheduler, onServerStopped);
    }

    public static void unregisterServerStoppedHook(Scheduler scheduler)
    {
        LISTENERS.remove(scheduler);
    }
}
