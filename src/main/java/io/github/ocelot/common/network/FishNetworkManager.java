package io.github.ocelot.common.network;

import io.github.ocelot.common.network.message.FishMessage;
import io.github.ocelot.common.network.message.LoginFishMessage;
import net.minecraft.util.LazyValue;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

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
public class FishNetworkManager
{
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
    private <MSG extends FishMessage<T>, T> SimpleChannel.MessageBuilder<MSG> getMessageBuilder(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        return this.channel.messageBuilder(clazz, this.nextId++, direction).encoder(FishMessage::writePacketData).decoder(buf ->
        {
            MSG msg = generator.get();
            msg.readPacketData(buf);
            return msg;
        }).consumer((message, ctx) ->
        {
            message.processPacket((T) (ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler.getValue().get() : this.serverMessageHandler.getValue().get()), ctx.get());
            return true;
        });
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
    public <MSG extends FishMessage<T>, T> void registerPlay(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
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
    public <MSG extends LoginFishMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, @Nullable NetworkDirection direction)
    {
        getMessageBuilder(clazz, generator, direction).loginIndex(LoginFishMessage::getAsInt, LoginFishMessage::setLoginIndex).markAsLoginPacket().add();
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
    public <MSG extends LoginFishMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable NetworkDirection direction)
    {
        getMessageBuilder(clazz, generator, direction).loginIndex(LoginFishMessage::getAsInt, LoginFishMessage::setLoginIndex).buildLoginPacketList(loginPacketGenerators).add();
    }
}
