package io.github.ocelot.sonar.common.network;

import com.google.common.base.Suppliers;
import io.github.ocelot.sonar.common.network.message.SonarLoginMessage;
import io.github.ocelot.sonar.common.network.message.SonarMessage;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraftforge.fmllegacy.network.FMLHandshakeHandler;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Manages the registering of network messages between the client and server.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public class SonarNetworkManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final SimpleChannel channel;
    private final Supplier<Supplier<Object>> clientMessageHandler;
    private final Supplier<Supplier<Object>> serverMessageHandler;
    private int nextId;

    public SonarNetworkManager(SimpleChannel channel, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory)
    {
        this.channel = channel;
        this.clientMessageHandler = Suppliers.memoize(() -> Suppliers.memoize(clientFactory::get));
        this.serverMessageHandler = Suppliers.memoize(() -> Suppliers.memoize(serverFactory::get));
    }

    @SuppressWarnings("unchecked")
    private <MSG extends SonarMessage<T>, T> boolean processMessage(MSG msg, Supplier<NetworkEvent.Context> ctx)
    {
        try
        {
            msg.processPacket((T) (ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler.get().get() : this.serverMessageHandler.get().get()), ctx.get());
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to process packet for class: " + msg.getClass().getName(), e);

            Component reason = new TranslatableComponent("disconnect.genericReason", "Internal Exception: " + e);
            Connection networkManager = ctx.get().getNetworkManager();
            PacketListener netHandler = networkManager.getPacketListener();
            boolean local = networkManager.isMemoryConnection();

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
        return true;
    }

    private <MSG extends SonarMessage<T>, T> SimpleChannel.MessageBuilder<MSG> getMessageBuilder(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        return this.channel.messageBuilder(clazz, this.nextId++, direction).encoder(SonarMessage::writePacketData).decoder(buf ->
        {
            MSG msg = generator.get();
            msg.readPacketData(buf);
            return msg;
        }).consumer((SimpleChannel.MessageBuilder.ToBooleanBiFunction<MSG, Supplier<NetworkEvent.Context>>) this::processMessage);
    }

    /**
     * Registers a message intended to be sent during the play network phase.
     *
     * @param clazz     The class of the message
     * @param generator The generator for a new message
     * @param direction The direction the message should be able to go or null for bi-directional
     * @param <MSG>     The type of message to be sent
     * @param <T>       The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    public <MSG extends SonarMessage<T>, T> void register(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        getMessageBuilder(clazz, generator, direction).add();
    }

    /**
     * Registers a message intended to be sent during the login network phase.
     *
     * @param clazz     The class of the message
     * @param generator The generator for a new message
     * @param direction The direction the message should be able to go or null for bi-directional
     * @param <MSG>     The type of message to be sent
     * @param <T>       The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    public <MSG extends SonarLoginMessage<T>, T> void registerLoginReply(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        this.channel.messageBuilder(clazz, this.nextId++, direction).encoder(SonarMessage::writePacketData).decoder(buf ->
                {
                    MSG msg = generator.get();
                    msg.readPacketData(buf);
                    return msg;
                })
                .consumer(FMLHandshakeHandler.indexFirst((__, msg, ctx) -> ctx.get().setPacketHandled(this.processMessage(msg, ctx))))
                .loginIndex(SonarLoginMessage::getAsInt, SonarLoginMessage::setLoginIndex)
                .add();
    }

    /**
     * Registers a message intended to be sent during the login network phase.
     *
     * @param clazz     The class of the message
     * @param generator The generator for a new message
     * @param direction The direction the message should be able to go or null for bi-directional
     * @param <MSG>     The type of message to be sent
     * @param <T>       The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    public <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        getMessageBuilder(clazz, generator, direction)
                .loginIndex(SonarLoginMessage::getAsInt, SonarLoginMessage::setLoginIndex)
                .markAsLoginPacket()
                .add();
    }

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
    public <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable NetworkDirection direction)
    {
        getMessageBuilder(clazz, generator, direction)
                .loginIndex(SonarLoginMessage::getAsInt, SonarLoginMessage::setLoginIndex)
                .buildLoginPacketList(loginPacketGenerators)
                .add();
    }
}
