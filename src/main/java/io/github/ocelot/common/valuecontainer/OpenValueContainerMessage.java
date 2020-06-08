package io.github.ocelot.common.valuecontainer;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

/**
 * <p>A pre-built message that handles the opening of {@link ValueContainer} screens.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class OpenValueContainerMessage
{
    private final BlockPos pos;

    public OpenValueContainerMessage(BlockPos pos)
    {
        this.pos = pos;
    }

    /**
     * Encodes the provided message into the specified buffer.
     *
     * @param msg The message to serialize
     * @param buf The buffer to write into
     */
    public static void encode(OpenValueContainerMessage msg, PacketBuffer buf)
    {
        buf.writeBlockPos(msg.pos);
    }

    /**
     * Decodes a new {@link OpenValueContainerMessage} from the provided buffer.
     *
     * @param buf The buffer to read from
     * @return A new deserialized message
     */
    public static OpenValueContainerMessage decode(PacketBuffer buf)
    {
        return new OpenValueContainerMessage(buf.readBlockPos());
    }

    /**
     * @return The position of the container
     */
    public BlockPos getPos()
    {
        return pos;
    }
}
