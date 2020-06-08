package io.github.ocelot.common.item;

import io.github.ocelot.common.valuecontainer.ValueContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

/**
 * <p>A default implementation of a value container editor item.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class ValueContainerEditorItem extends Item
{
    private final BiConsumer<ServerPlayerEntity, BlockPos> packetSender;

    public ValueContainerEditorItem(Properties properties, BiConsumer<ServerPlayerEntity, BlockPos> packetSender)
    {
        super(properties);
        this.packetSender = packetSender;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isCreative())
        {
            if (world.getTileEntity(pos) instanceof ValueContainer || world.getBlockState(pos).getBlock() instanceof ValueContainer)
            {
                if (!world.isRemote())
                    this.packetSender.accept((ServerPlayerEntity) player, pos);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }
}
