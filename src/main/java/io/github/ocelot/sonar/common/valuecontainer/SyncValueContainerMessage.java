package io.github.ocelot.sonar.common.valuecontainer;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
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
public class SyncValueContainerMessage implements SonarMessage<IValueContainerServerHandler>
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
        this.data = buf.readNbt();
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(this.data);
    }

    @Override
    public void processPacket(IValueContainerServerHandler handler, NetworkEvent.Context ctx)
    {
        handler.handleSyncValueContainerMessage(this, ctx);
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
