package io.github.ocelot.sonar.core.network;

import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import io.github.ocelot.sonar.common.network.message.SonarPacketDirection;
import net.minecraft.network.Connection;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@ApiStatus.Internal
public class ForgeSonarNetworkContext implements SonarNetworkContext
{
    private final Supplier<NetworkEvent.Context> ctx;

    public ForgeSonarNetworkContext(Supplier<NetworkEvent.Context> ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable runnable)
    {
        return this.ctx.get().enqueueWork(runnable);
    }

    @Override
    public SonarPacketDirection getDirection()
    {
        NetworkDirection direction = this.ctx.get().getDirection();
        switch (direction)
        {
            case PLAY_TO_SERVER:
                return SonarPacketDirection.PLAY_SERVERBOUND;
            case PLAY_TO_CLIENT:
                return SonarPacketDirection.PLAY_CLIENTBOUND;
            case LOGIN_TO_SERVER:
                return SonarPacketDirection.LOGIN_SERVERBOUND;
            case LOGIN_TO_CLIENT:
                return SonarPacketDirection.LOGIN_CLIENTBOUND;
            default:
                throw new IllegalStateException("Unknown network direction: " + direction);
        }
    }

    @Override
    public Connection getNetworkManager()
    {
        return this.ctx.get().getNetworkManager();
    }

    public NetworkEvent.Context getContext()
    {
        return this.ctx.get();
    }
}
