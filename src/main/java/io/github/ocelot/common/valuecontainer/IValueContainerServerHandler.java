package io.github.ocelot.common.valuecontainer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>Manages the receiving of value container messages on the server side.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface IValueContainerServerHandler
{
    Logger LOGGER = LogManager.getLogger();

    /**
     * Called when the server tells the server to sync data from the screen to the block.
     *
     * @param msg The message received
     * @param ctx The message context
     */
    default void handleSyncValueContainerMessage(SyncValueContainerMessage msg, NetworkEvent.Context ctx)
    {
        ServerPlayerEntity player = ctx.getSender();

        ctx.enqueueWork(() ->
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
    }
}
