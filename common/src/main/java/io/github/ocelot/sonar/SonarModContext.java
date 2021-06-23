package io.github.ocelot.sonar;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.util.thread.BlockableEventLoop;

/**
 * <p>The context used for a mod during Sonar loading.</p>
 *
 * @author Ocelot
 * @since 7.0.0
 */
public interface SonarModContext
{
    /**
     * <p>Retrieves the Sonar context for the current mod. Each platform requires different parameters.</p>
     * <p><b>Forge:</b> should be invoked using the mod context <code>SonarModContext.get(ModLoadingContext.get())</code></p>
     * <p><b>Fabric:</b> should be invoked using the mod id <code>SonarModContext.get("modid")</code></p>
     *
     * @param parameters The additional platform-specific parameters to pass
     * @return A mod context for the current platform
     */
    @ExpectPlatform
    static SonarModContext get(Object... parameters)
    {
        throw new AssertionError();
    }

    /**
     * @return The id of the mod hosting Sonar
     */
    String getParentModId();

    /**
     * Retrieves the executor for the specific side instance. This should only be used in common code where the current server is unknown.
     *
     * @param client Whether or not to fetch the client executor
     * @return The executor for the specific side
     */
    BlockableEventLoop<?> getSidedExecutor(boolean client);
}
