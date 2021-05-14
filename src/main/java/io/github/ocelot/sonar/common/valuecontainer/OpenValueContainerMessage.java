package io.github.ocelot.sonar.common.valuecontainer;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;

/**
 * <p>A pre-built message that handles the opening of {@link ValueContainer} screens.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class OpenValueContainerMessage implements SonarMessage<IValueContainerClientHandler>
{
    private BlockPos pos;
    private CompoundNBT nbt;

    public OpenValueContainerMessage()
    {
    }

    public OpenValueContainerMessage(World world, BlockPos pos)
    {
        this.pos = pos;
        this.nbt = ValueContainer.get(world, pos).map(container -> container.writeClientValueContainer(world, pos)).orElse(null);
    }

    @Override
    public void readPacketData(PacketBuffer buf)
    {
        this.pos = buf.readBlockPos();
        this.nbt = buf.readBoolean() ? buf.readNbt() : null;
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.nbt != null);
        if (this.nbt != null)
            buf.writeNbt(this.nbt);
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

    /**
     * @return The additional client data with the container
     */
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public CompoundNBT getNbt()
    {
        return nbt;
    }
}
