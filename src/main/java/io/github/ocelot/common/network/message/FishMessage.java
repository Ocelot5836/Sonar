package io.github.ocelot.common.network.message;

import io.github.ocelot.common.network.IFishMessageHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * <p>A message intended for the specified message handler.</p>
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 3.2.0
 */
public interface FishMessage<T extends IFishMessageHandler>
{
    /**
     * Reads the raw message data from the data stream.
     *
     * @param buf The buffer to read from
     */
    void readPacketData(PacketBuffer buf);

    /**
     * Writes the raw message data to the data stream.
     *
     * @param buf The buffer to write to
     */
    void writePacketData(PacketBuffer buf);

    /**
     * Passes this message into the specified {@link IFishMessageHandler} to process the message.
     *
     * @param handler The handler to process the message
     * @param ctx     The context of the message
     */
    void processPacket(T handler, NetworkEvent.Context ctx);
}
