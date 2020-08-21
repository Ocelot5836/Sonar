package io.github.ocelot.common.valuecontainer;

import io.github.ocelot.common.network.message.FishMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * <p>A pre-built message that handles the opening of {@link ValueContainer} screens.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class OpenValueContainerMessage implements FishMessage<IValueContainerClientHandler>
{
    private BlockPos pos;

    public OpenValueContainerMessage()
    {
    }

    public OpenValueContainerMessage(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void readPacketData(PacketBuffer buf)
    {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void processPacket(IValueContainerClientHandler handler, NetworkEvent.Context ctx)
    {
        handler.handleOpenValueContainerMessage(this, ctx);
    }

    /**
     * @return The position of the container
     */
    @OnlyIn(Dist.CLIENT)
    public BlockPos getPos()
    {
        return pos;
    }
}
