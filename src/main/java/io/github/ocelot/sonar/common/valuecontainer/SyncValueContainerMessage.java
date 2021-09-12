package io.github.ocelot.sonar.common.valuecontainer;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;

/**
 * <p>A pre-built message that handles the serialization and deserialization of {@link ValueContainer} serialization messages.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 * @deprecated TODO remove in 7.0.0
 */
public class SyncValueContainerMessage implements SonarMessage<IValueContainerServerHandler>
{
    private BlockPos pos;
    private CompoundTag data;

    public SyncValueContainerMessage()
    {
    }

    public SyncValueContainerMessage(BlockPos pos, List<ValueContainerEntry<?>> entries)
    {
        this(pos, ValueContainer.serialize(entries));
    }

    public SyncValueContainerMessage(BlockPos pos, CompoundTag data)
    {
        this.pos = pos;
        this.data = data;
    }

    @Override
    public void readPacketData(FriendlyByteBuf buf)
    {
        this.pos = buf.readBlockPos();
        this.data = buf.readNbt();
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf)
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
    public CompoundTag getData()
    {
        return data;
    }
}
