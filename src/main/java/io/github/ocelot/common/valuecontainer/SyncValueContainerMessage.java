package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * <p>A pre-built message that handles the serialization and deserialization of {@link ValueContainer} serialization messages.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class SyncValueContainerMessage
{
    private final BlockPos pos;
    private final CompoundNBT data;

    @Deprecated
    public SyncValueContainerMessage(ValueContainer container, List<ValueContainerEntry<?>> entries)
    {
        this(container.getContainerPos(), ValueContainer.serialize(container, entries));
    }

    public SyncValueContainerMessage(ValueContainer container, BlockPos pos, List<ValueContainerEntry<?>> entries)
    {
        this(pos, ValueContainer.serialize(container, entries));
    }

    public SyncValueContainerMessage(BlockPos pos, CompoundNBT data)
    {
        this.pos = pos;
        this.data = data;
    }

    /**
     * Encodes the provided message into the specified buffer.
     *
     * @param msg The message to serialize
     * @param buf The buffer to write into
     */
    public static void encode(SyncValueContainerMessage msg, PacketBuffer buf)
    {
        buf.writeBlockPos(msg.pos);
        buf.writeCompoundTag(msg.data);
    }

    /**
     * Decodes a new {@link SyncValueContainerMessage} from the provided buffer.
     *
     * @param buf The buffer to read from
     * @return A new, deserialized message
     */
    public static SyncValueContainerMessage decode(PacketBuffer buf)
    {
        return new SyncValueContainerMessage(buf.readBlockPos(), buf.readCompoundTag());
    }

    /**
     * @return The position of the container
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * @return The tag full of container data
     */
    public CompoundNBT getData()
    {
        return data;
    }
}
