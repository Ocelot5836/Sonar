package io.github.ocelot.common.network;

import io.github.ocelot.common.network.message.FishLoginMessage;
import io.github.ocelot.common.network.message.FishMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;
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
@SuppressWarnings("unused")
public class FishNetworkManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final SimpleChannel channel;
    private final LazyValue<Supplier<Object>> clientMessageHandler;
    private final LazyValue<Supplier<Object>> serverMessageHandler;
    private int nextId;

    public FishNetworkManager(SimpleChannel channel, Supplier<Supplier<Object>> clientSupplier, Supplier<Supplier<Object>> serverSupplier)
    {
        this.channel = channel;
        this.clientMessageHandler = new LazyValue<>(clientSupplier);
        this.serverMessageHandler = new LazyValue<>(serverSupplier);
    }

    @SuppressWarnings("unchecked")
    private <MSG extends FishMessage<T>, T> boolean processMessage(MSG msg, Supplier<NetworkEvent.Context> ctx)
    {
        try
        {
            msg.processPacket((T) (ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler.getValue().get() : this.serverMessageHandler.getValue().get()), ctx.get());
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to process packet for class: " + msg.getClass().getName(), e);
            if (ctx.get().getDirection().getReceptionSide().isServer())
            {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null)
                {
                    ITextComponent textComponent = new TranslationTextComponent("disconnect.genericReason", "Internal Exception: " + e);
                    NetworkManager networkManager = ctx.get().getNetworkManager();
                    networkManager.sendPacket(new SDisconnectPacket(textComponent), future -> networkManager.closeChannel(textComponent));
                    networkManager.disableAutoRead();
                }
            }
            return false;
        }
    }

    private <MSG extends FishMessage<T>, T> SimpleChannel.MessageBuilder<MSG> getMessageBuilder(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        return this.channel.messageBuilder(clazz, this.nextId++, direction).encoder(FishMessage::writePacketData).decoder(buf ->
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
    public <MSG extends FishMessage<T>, T> void register(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
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
    public <MSG extends FishLoginMessage<T>, T> void registerLoginReply(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        this.channel.messageBuilder(clazz, this.nextId++, direction).encoder(FishMessage::writePacketData).decoder(buf ->
        {
            MSG msg = generator.get();
            msg.readPacketData(buf);
            return msg;
        })
                .consumer(FMLHandshakeHandler.indexFirst((__, msg, ctx) -> ctx.get().setPacketHandled(this.processMessage(msg, ctx))))
                .loginIndex(FishLoginMessage::getAsInt, FishLoginMessage::setLoginIndex)
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
    public <MSG extends FishLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        getMessageBuilder(clazz, generator, direction)
                .loginIndex(FishLoginMessage::getAsInt, FishLoginMessage::setLoginIndex)
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
    public <MSG extends FishLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable NetworkDirection direction)
    {
        getMessageBuilder(clazz, generator, direction)
                .loginIndex(FishLoginMessage::getAsInt, FishLoginMessage::setLoginIndex)
                .buildLoginPacketList(loginPacketGenerators)
                .add();
    }
}
