package io.github.ocelot.sonar.common.valuecontainer;

import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>Manages the receiving of value container messages on the server side.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface ValueContainerServerHandler
{
    Logger LOGGER = LogManager.getLogger();

    /**
     * Called when the server tells the server to sync data from the screen to the block.
     *
     * @param msg The message received
     * @param ctx The message context
     */
    default void handleSyncValueContainerMessage(SyncValueContainerMessage msg, SonarNetworkContext ctx)
    {
        ServerPlayer player = ctx.getSender();

        ctx.enqueueWork(() ->
        {
            if (player == null)
                return;
            Level world = player.level;
            BlockPos pos = msg.getPos();

            BlockEntity te = world.getBlockEntity(pos);
            if (!(te instanceof ValueContainer) && !(world.getBlockState(pos).getBlock() instanceof ValueContainer))
            {
                LOGGER.error("Tile Entity or Block at '" + pos + "' was expected to be a ValueContainer, but it was " + te + "!");
                return;
            }

            if (!player.canUseGameMasterBlocks())
            {
                LOGGER.error("Player with id " + player.getUUID() + " does not have the permission to modify value containers!");
                return;
            }

            ValueContainer.deserialize(world, pos, te instanceof ValueContainer ? (ValueContainer) te : (ValueContainer) world.getBlockState(pos).getBlock(), msg.getData());
        });
    }
}
