package io.github.ocelot.sonar;

import io.github.ocelot.sonar.core.network.SonarInbuiltMessages;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Allows the enabling of specific features when Sonar is initialized.</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public enum SonarModule
{
    INBUILT_NETWORK(false, SonarInbuiltMessages::register, null);

    private final boolean clientOnly;
    private final Runnable init;
    private final Runnable setup;

    SonarModule(boolean clientOnly, @Nullable Runnable init, @Nullable Runnable setup)
    {
        this.clientOnly = clientOnly;
        this.init = init;
        this.setup = setup;
    }

    void init()
    {
        if (this.init != null)
            this.init.run();
    }

    void setup()
    {
        if (this.setup != null)
            this.setup.run();
    }

    /**
     * @return Whether or not this module can only be loaded on the common side.
     */
    public boolean isCommonOnly()
    {
        return !clientOnly;
    }

    /**
     * @return Whether or not this module can only be loaded client side.
     */
    public boolean isClientOnly()
    {
        return clientOnly;
    }
}
