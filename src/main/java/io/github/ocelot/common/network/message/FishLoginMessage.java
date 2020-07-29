package io.github.ocelot.common.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.IntSupplier;

/**
 * <p>An implementation of {@link FishMessage} intended for login messages.</p>
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 3.2.0
 */
public interface FishLoginMessage<T> extends FishMessage<T>, IntSupplier
{
    /**
     * Reads the raw message data from the data stream.
     *
     * @param buf The buffer to read from
     */
    default void readLoginPacketData(PacketBuffer buf)
    {
        this.readPacketData(buf);
    }

    /**
     * Writes the raw message data to the data stream.
     *
     * @param buf The buffer to write to
     */
    default void writeLoginPacketData(PacketBuffer buf)
    {
        this.writePacketData(buf);
    }

    /**
     * Passes this message into the specified handler to process the message.
     *
     * @param handler The handler to process the message
     * @param ctx     The context of the message
     */
    default void processLoginPacket(T handler, NetworkEvent.Context ctx)
    {
        this.processPacket(handler, ctx);
    }

    /**
     * Sets the index for the login message. Should not usually be called.
     *
     * @param index The new login index
     */
    void setLoginIndex(int index);
}
