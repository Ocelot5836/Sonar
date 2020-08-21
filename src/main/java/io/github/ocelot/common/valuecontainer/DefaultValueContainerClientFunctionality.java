package io.github.ocelot.common.valuecontainer;

import io.github.ocelot.client.screen.ValueContainerEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * <p>Helps with creating a {@link ValueContainer} framework.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 * @deprecated Use {@link IValueContainerClientHandler} instead TODO remove in 4.0.0
 */
public class DefaultValueContainerClientFunctionality
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static Factory screenFactory;

    /**
     * Sends a sync message to the server on behalf of the specified channel.
     *
     * @param channel The channel to send the packet
     * @param screen  The screen to sync data with
     */
    public static void sendDataToServer(SimpleChannel channel, ValueContainerEditorScreen screen)
    {
        channel.send(PacketDistributor.SERVER.noArg(), screen.createSyncMessage());
    }

    /**
     * Handles receiving the {@link OpenValueContainerMessage} for the default implementation.
     *
     * @param msg The message received
     * @param ctx The network context
     */
    @OnlyIn(Dist.CLIENT)
    public static void handleOpenGuiMessage(OpenValueContainerMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        if (screenFactory == null)
        {
            LOGGER.error("No screen factory has been set!");
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        World world = minecraft.world;

        ctx.get().enqueueWork(() ->
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

            Screen screen = screenFactory.create(te instanceof ValueContainer ? (ValueContainer) te : (ValueContainer) world.getBlockState(pos).getBlock(), pos);
            if (screen == null)
                return;

            minecraft.displayGuiScreen(screen);
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * Sets the factory for what creates a new screen. This is required if {@link #handleOpenGuiMessage(OpenValueContainerMessage, Supplier)} is used.
     *
     * @param screenFactory The new screen factory
     */
    @OnlyIn(Dist.CLIENT)
    public static void setScreenFactory(Factory screenFactory)
    {
        DefaultValueContainerClientFunctionality.screenFactory = screenFactory;
    }

    /**
     * <p>A factory for creating a new {@link Screen} for a {@link ValueContainer}</p>
     *
     * @author Ocelot
     */
    @OnlyIn(Dist.CLIENT)
    public interface Factory
    {
        /**
         * Creates a new screen instance for the specified value container.
         *
         * @param valueContainer The container to set the screen for
         * @param pos            The position of the container
         * @return The new screen or null if no screen is requested
         */
        @Nullable
        Screen create(ValueContainer valueContainer, BlockPos pos);
    }
}
