package io.github.ocelot.sonar.common.network.message;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * <p>Context for a {@link SonarMessage} that acts as an intermediary between platforms.</p>
 *
 * @author Ocelot
 * @since 7.0.0
 */
public interface SonarNetworkContext
{
    void reply(SonarMessage<?> message);

    /**
     * Defers work if not on the main thread.
     *
     * @param runnable The task to execute
     * @return A future that will complete when the task is done
     */
    CompletableFuture<Void> enqueueWork(Runnable runnable);

    /**
     * @return The direction the packet is going
     */
    SonarPacketDirection getDirection();

    /**
     * Fetches the server sided player only if the packet has been received server side.
     *
     * @return The player or <code>null</code> if the current handler is not {@link SonarPacketDirection#PLAY_SERVERBOUND}
     */
    @Nullable
    default ServerPlayer getSender()
    {
        PacketListener netHandler = this.getNetworkManager().getPacketListener();
        if (netHandler instanceof ServerGamePacketListenerImpl)
        {
            ServerGamePacketListenerImpl netHandlerPlayServer = (ServerGamePacketListenerImpl) netHandler;
            return netHandlerPlayServer.player;
        }
        return null;
    }

    /**
     * @return The current connection between the client and server
     */
    Connection getNetworkManager();
}
