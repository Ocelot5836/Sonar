package io.github.ocelot.sonar.common.network;

import io.github.ocelot.sonar.common.network.message.SonarLoginMessage;
import io.github.ocelot.sonar.common.network.message.SonarMessage;
import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import io.github.ocelot.sonar.common.network.message.SonarPacketDirection;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Manages the registering of network messages between the client and server.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface SonarNetworkChannel
{
    /**
     * Creates a new network channel with the specified id and client/server packet handlers.
     *
     * @param channelId     The id of the channel
     * @param clientFactory The factory to create a new client packet handler
     * @param serverFactory The factory to create a new server packet handler
     * @return A multi-platform network channel
     */
    @ExpectPlatform
    static SonarNetworkChannel create(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory)
    {
        throw new AssertionError();
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    static <MSG extends SonarMessage<T>, T> void processMessage(@NotNull MSG msg, SonarNetworkContext context, Object handler)
    {
        try
        {
            msg.processPacket((T) handler, context);
        }
        catch (Exception e)
        {
            LogManager.getLogger().error("Failed to process packet for class: " + msg.getClass().getName(), e);

            Component reason = new TranslatableComponent("disconnect.genericReason", "Internal Exception: " + e);
            Connection networkManager = context.getNetworkManager();
            PacketListener netHandler = networkManager.getPacketListener();

            // Need to check the channel type to determine how to disconnect
            if (netHandler instanceof ServerStatusPacketListener)
                networkManager.disconnect(reason);
            if (netHandler instanceof ServerLoginPacketListenerImpl)
                ((ServerLoginPacketListenerImpl) netHandler).disconnect(reason);
            if (netHandler instanceof ServerGamePacketListenerImpl)
                ((ServerGamePacketListenerImpl) netHandler).disconnect(reason);
            if (netHandler instanceof ClientStatusPacketListener)
            {
                networkManager.disconnect(reason);
                netHandler.onDisconnect(reason);
            }
            if (netHandler instanceof ClientLoginPacketListener)
            {
                networkManager.disconnect(reason);
                netHandler.onDisconnect(reason);
            }
        }
    }

    void reply(SonarMessage<?> message, SonarNetworkContext context);

    void sendTo(ServerPlayer player, SonarMessage<?> message);

    void sendTo(ServerLevel level, SonarMessage<?> message);

    void sendToNear(ServerLevel level, double x, double y, double z, double radius, SonarMessage<?> message);

    void sendToAll(MinecraftServer server, SonarMessage<?> message);

    void sendToServer(SonarMessage<?> message);

    void sendToTracking(Entity entity, SonarMessage<?> message);

    void sendToTracking(ServerLevel level, BlockPos pos, SonarMessage<?> message);

    void sendToTracking(ServerLevel level, ChunkPos pos, SonarMessage<?> message);

    void sendToTrackingAndSelf(Entity entity, SonarMessage<?> message);

    /**
     * Registers a message intended to be sent during the play network phase.
     *
     * @param clazz     The class of the message
     * @param generator The generator for a new message
     * @param direction The direction the message should be able to go or null for bi-directional
     * @param <MSG>     The type of message to be sent
     * @param <T>       The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    <MSG extends SonarMessage<T>, T> void register(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction);

    /**
     * Registers a message intended to be sent during the login network phase.
     *
     * @param clazz     The class of the message
     * @param generator The generator for a new message
     * @param direction The direction the message should be able to go or null for bi-directional
     * @param <MSG>     The type of message to be sent
     * @param <T>       The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    <MSG extends SonarLoginMessage<T>, T> void registerLoginReply(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction);

    /**
     * Registers a message intended to be sent during the login network phase.
     *
     * @param clazz     The class of the message
     * @param generator The generator for a new message
     * @param direction The direction the message should be able to go or null for bi-directional
     * @param <MSG>     The type of message to be sent
     * @param <T>       The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction);

    /**
     * Registers a message intended to be sent during the login network phase. Allows the custom definition of login packets.
     *
     * @param clazz                 The class of the message
     * @param generator             The generator for a new message
     * @param loginPacketGenerators The function to generate login packets
     * @param direction             The direction the message should be able to go or null for bi-directional
     * @param <MSG>                 The type of message to be sent
     * @param <T>                   The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable SonarPacketDirection direction);
}
