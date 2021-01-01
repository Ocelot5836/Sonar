package io.github.ocelot.sonar.common.item;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.SonarModule;
import io.github.ocelot.sonar.common.network.inbuilt.SonarInbuiltMessages;
import io.github.ocelot.sonar.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

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
     * @return Whether or not that player can use this container editor
     */
    protected boolean canPlayerUse(ValueContainer valueContainer, World world, BlockPos pos, PlayerEntity player)
    {
        return player.canUseCommandBlock();
    }

    /**
     * Sends a packet to the client to open a new screen.
     *
     * @param valueContainer The value container being opened
     * @param world          The world the container is in
     * @param pos            The position of the container
     * @param player         The player who opened the container
     * @return Whether or not the packet was sent successfully
     */
    protected boolean sendPacket(ValueContainer valueContainer, ServerWorld world, BlockPos pos, ServerPlayerEntity player)
    {
        if (!Sonar.isModuleLoaded(SonarModule.INBUILT_NETWORK))
            throw new IllegalStateException("There is no implementation to send a packet! Enable INBUILT_NETWORK Sonar Module to automatically handle.");
        SonarInbuiltMessages.PLAY.send(PacketDistributor.PLAYER.with(() -> player), new OpenValueContainerMessage(world, pos));
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isCreative())
        {
            Optional<ValueContainer> valueContainerOptional = ValueContainer.get(world, pos);
            if (valueContainerOptional.isPresent())
            {
                ValueContainer valueContainer = valueContainerOptional.get();
                if (!world.isRemote())
                {
                    if (!this.canPlayerUse(valueContainer, world, pos, player))
                    {
                        player.sendStatusMessage(new TranslationTextComponent(this.getTranslationKey(context.getItem()) + ".cannot_edit"), false);
                        return ActionResultType.SUCCESS;
                    }
                    if (this.sendPacket(valueContainer, (ServerWorld) world, pos, (ServerPlayerEntity) player))
                        return ActionResultType.SUCCESS;
                }
                else if (this.canPlayerUse(valueContainer, world, pos, player))
                {
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.FAIL;
    }
}
