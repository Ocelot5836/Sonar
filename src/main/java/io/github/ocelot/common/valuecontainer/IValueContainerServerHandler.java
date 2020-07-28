package io.github.ocelot.common.valuecontainer;

import net.minecraftforge.fml.network.NetworkEvent;

/**
 * <p>Manages the receiving of value container messages on the server side.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface IValueContainerServerHandler
{
    /**
     * Called when the server tells the server to sync data from the screen to the block.
     *
     * @param msg The message received
     * @param ctx The message context
     */
    void handleSyncValueContainerMessage(SyncValueContainerMessage msg, NetworkEvent.Context ctx);
}
