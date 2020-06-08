package io.github.ocelot.common.valuecontainer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * <p>Helps with creating a {@link ValueContainer} framework.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class DefaultValueContainerServerFunctionality
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Handles receiving the {@link SyncValueContainerMessage} for the default implementation.
     *
     * @param msg The message received
     * @param ctx The network context
     */
    public static void handleSyncValueContainerMessage(SyncValueContainerMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        if (ctx.get().getDirection().getReceptionSide().isClient())
            return;

        ServerPlayerEntity player = ctx.get().getSender();

        ctx.get().enqueueWork(() ->
        {
            if (player == null)
                return;
            World world = player.world;
            BlockPos pos = msg.getPos();

            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof ValueContainer) && !(world.getBlockState(pos).getBlock() instanceof ValueContainer))
            {
                LOGGER.error("Tile Entity or Block at '" + pos + "' was expected to be a ValueContainer, but it was " + te + "!");
                return;
            }

            if (!player.canUseCommandBlock())
            {
                LOGGER.error("Player with id " + player.getUniqueID() + " does not have the permission to modify value containers!");
                return;
            }

            ValueContainer.deserialize(world, pos, te instanceof ValueContainer ? (ValueContainer) te : (ValueContainer) world.getBlockState(pos).getBlock(), msg.getData());
        });
        ctx.get().setPacketHandled(true);
    }
}
