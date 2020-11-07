package io.github.ocelot.sonar.item;

import io.github.ocelot.sonar.network.CTestPlayMessage;
import io.github.ocelot.sonar.network.STestPlayMessage;
import io.github.ocelot.sonar.network.TestMessageHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class CrashItem extends Item
{
    public CrashItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        if (world.isRemote())
        {
            TestMessageHandler.PLAY.send(PacketDistributor.SERVER.noArg(), new CTestPlayMessage());
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }
}
