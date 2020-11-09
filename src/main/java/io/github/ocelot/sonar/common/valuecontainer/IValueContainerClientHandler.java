package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;

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
            if (world == null)
                return;

            BlockPos pos = msg.getPos();
            CompoundNBT nbt = msg.getNbt();

            Optional<ValueContainer> valueContainerOptional = ValueContainer.get(world, pos);
            if (!valueContainerOptional.isPresent())
            {
                LOGGER.error("ValueContainer was expected at '" + pos + "', but it was not present!");
                return;
            }

            if (nbt != null)
                valueContainerOptional.get().readClientValueContainer(world, pos, nbt);

            Screen screen = this.createValueContainerScreen(valueContainerOptional.get(), pos);
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
    default Screen createValueContainerScreen(ValueContainer container, BlockPos pos)
    {
        return null;
    }
}
