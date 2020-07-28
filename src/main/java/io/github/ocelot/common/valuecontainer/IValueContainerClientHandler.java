package io.github.ocelot.common.valuecontainer;

import net.minecraftforge.fml.network.NetworkEvent;

/**
 * <p>Manages the receiving of value container messages on the client side.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface IValueContainerClientHandler
{
    /**
     * Called when the server tells the client to open a value container screen.
     *
     * @param msg The message received
     * @param ctx The message context
     */
    void handleOpenValueContainerMessage(OpenValueContainerMessage msg, NetworkEvent.Context ctx);
}
