package io.github.ocelot.common.valuecontainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * <p>Manages the receiving of value container messages on the client side.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface IValueContainerClientHandler
{
    Logger LOGGER = LogManager.getLogger();

    /**
     * Called when the server tells the client to open a value container screen.
     *
     * @param msg The message received
     * @param ctx The message context
     */
    default void handleOpenValueContainerMessage(OpenValueContainerMessage msg, NetworkEvent.Context ctx)
    {
        Minecraft minecraft = Minecraft.getInstance();
        World world = minecraft.world;

        ctx.enqueueWork(() ->
        {
            BlockPos pos = msg.getPos();

            if (world == null)
                return;

            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof ValueContainer) && !(world.getBlockState(pos).getBlock() instanceof ValueContainer))
            {
                LOGGER.error("Tile Entity at '" + pos + "' was expected to be a ValueContainer, but it was " + world.getTileEntity(pos) + "!");
                return;
            }

            Screen screen = this.createValueContainerScreen(te instanceof ValueContainer ? (ValueContainer) te : (ValueContainer) world.getBlockState(pos).getBlock(), pos);
            if (screen == null)
                return;

            minecraft.displayGuiScreen(screen);
        });
    }

    /**
     * Creates a new screen instance for the specified value container.
     *
     * @param container The container to set the screen for
     * @param pos       The position of the container
     * @return The new screen or null if no screen is requested
     */
    @Nullable
    Screen createValueContainerScreen(ValueContainer container, BlockPos pos);
}
