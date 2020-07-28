package io.github.ocelot.common.valuecontainer;

import io.github.ocelot.common.network.message.FishMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;

/**
 * <p>A pre-built message that handles the serialization and deserialization of {@link ValueContainer} serialization messages.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class SyncValueContainerMessage implements FishMessage<IValueContainerServerHandler>
{
    private BlockPos pos;
    private CompoundNBT data;

    public SyncValueContainerMessage()
    {
    }

    public SyncValueContainerMessage(BlockPos pos, List<ValueContainerEntry<?>> entries)
    {
        this(pos, ValueContainer.serialize(entries));
    }

    public SyncValueContainerMessage(BlockPos pos, CompoundNBT data)
    {
        this.pos = pos;
        this.data = data;
    }

    @Override
    public void readPacketData(PacketBuffer buf)
    {
        this.pos = buf.readBlockPos();
        this.data = buf.readCompoundTag();
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeBlockPos(this.pos);
        buf.writeCompoundTag(this.data);
    }

    @Override
    public void processPacket(IValueContainerServerHandler handler, NetworkEvent.Context ctx)
    {
        handler.handleSyncValueContainerMessage(this, ctx);
    }

    /**
     * Encodes the provided message into the specified buffer.
     *
     * @param msg The message to serialize
     * @param buf The buffer to write into
     * @deprecated TODO remove in 4.0.0
     */
    public static void encode(SyncValueContainerMessage msg, PacketBuffer buf)
    {
        msg.writePacketData(buf);
    }

    /**
     * Decodes a new {@link SyncValueContainerMessage} from the provided buffer.
     *
     * @param buf The buffer to read from
     * @return A new, deserialized message
     * @deprecated TODO remove in 4.0.0
     */
    public static SyncValueContainerMessage decode(PacketBuffer buf)
    {
        SyncValueContainerMessage msg = new SyncValueContainerMessage();
        msg.readPacketData(buf);
        return msg;
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
