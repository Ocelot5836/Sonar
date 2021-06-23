package io.github.ocelot.sonar.core.network;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import io.github.ocelot.sonar.common.network.message.SonarPacketDirection;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class FabricSonarNetworkContext implements SonarNetworkContext
{
    private final Connection connection;
    private final SonarPacketDirection direction;

    public FabricSonarNetworkContext(Connection connection, SonarPacketDirection direction)
    {
        this.connection = connection;
        this.direction = direction;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable runnable)
    {
        return Sonar.context().getSidedExecutor(this.getDirection().isClientbound()).submit(runnable);
    }

    @Override
    public SonarPacketDirection getDirection()
    {
        return direction;
    }

    @Override
    public Connection getNetworkManager()
    {
        return connection;
    }
}
