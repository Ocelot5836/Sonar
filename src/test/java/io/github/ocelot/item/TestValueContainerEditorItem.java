package io.github.ocelot.item;

import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.network.DisplayScreenMessage;
import io.github.ocelot.network.TestMessageHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author Ocelot
 */
public class TestValueContainerEditorItem extends Item
{
    public TestValueContainerEditorItem(Item.Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isCreative())
        {
            if (world.getBlockState(pos).getBlock() instanceof ValueContainer || world.getTileEntity(pos) instanceof ValueContainer)
            {
                if (!world.isRemote())
                    TestMessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new DisplayScreenMessage(DisplayScreenMessage.GuiType.VALUE_CONTAINER_EDITOR, pos));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }
}
