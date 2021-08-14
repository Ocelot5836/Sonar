package io.github.ocelot.sonar.common.item;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.SonarModule;
import io.github.ocelot.sonar.common.network.inbuilt.SonarInbuiltMessages;
import io.github.ocelot.sonar.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.Optional;

/**
 * <p>A default implementation of a value container editor item.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class ValueContainerEditorItem extends Item
{
    public ValueContainerEditorItem(Properties properties)
    {
        super(properties);
    }

    /**
     * Whether or not the specified player is permitted to use the value container editor.
     *
     * @param world  The world the player is in
     * @param pos    The position the player clicked
     * @param player The player to check
     * @return Whether that player can use this container editor
     */
    protected boolean canPlayerUse(ValueContainer valueContainer, Level world, BlockPos pos, Player player)
    {
        return player.canUseGameMasterBlocks();
    }

    /**
     * Sends a packet to the client to open a new screen.
     *
     * @param valueContainer The value container being opened
     * @param world          The world the container is in
     * @param pos            The position of the container
     * @param player         The player who opened the container
     * @return Whether the packet was sent successfully
     */
    protected boolean sendPacket(ValueContainer valueContainer, ServerLevel world, BlockPos pos, ServerPlayer player)
    {
        if (!Sonar.isModuleLoaded(SonarModule.INBUILT_NETWORK))
            throw new IllegalStateException("There is no implementation to send a packet! Enable INBUILT_NETWORK Sonar Module to automatically handle.");
        SonarInbuiltMessages.PLAY.send(PacketDistributor.PLAYER.with(() -> player), new OpenValueContainerMessage(world, pos));
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player != null && player.isCreative())
        {
            Optional<ValueContainer> valueContainerOptional = ValueContainer.get(world, pos);
            if (valueContainerOptional.isPresent())
            {
                ValueContainer valueContainer = valueContainerOptional.get();
                if (!world.isClientSide())
                {
                    if (!this.canPlayerUse(valueContainer, world, pos, player))
                    {
                        player.displayClientMessage(new TranslatableComponent(this.getDescriptionId(context.getItemInHand()) + ".cannot_edit"), false);
                        return InteractionResult.SUCCESS;
                    }
                    if (this.sendPacket(valueContainer, (ServerLevel) world, pos, (ServerPlayer) player))
                        return InteractionResult.SUCCESS;
                }
                else if (this.canPlayerUse(valueContainer, world, pos, player))
                {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }
}
