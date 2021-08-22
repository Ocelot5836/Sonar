package io.github.ocelot.sonar;

import io.github.ocelot.sonar.client.shader.ShaderLoader;
import io.github.ocelot.sonar.common.network.inbuilt.SonarInbuiltMessages;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * <p>Allows the enabling of specific features when Sonar is initialized.</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public enum SonarModule
{
    INBUILT_NETWORK(false, SonarInbuiltMessages::register, null),
    SHADER(true, ShaderLoader::init, null);

    private final boolean clientOnly;
    private final Consumer<IEventBus> init;
    private final Runnable setup;

    SonarModule(boolean clientOnly, @Nullable Consumer<IEventBus> init, @Nullable Runnable setup)
    {
        this.clientOnly = clientOnly;
        this.init = init;
        this.setup = setup;
    }

    void init(IEventBus bus)
    {
        if (this.init != null)
            this.init.accept(bus);
    }

    void setup()
    {
        if (this.setup != null)
            this.setup.run();
    }

    /**
     * @return Whether or not this module can only be loaded client side.
     */
    public boolean isClientOnly()
    {
        return clientOnly;
    }
}
