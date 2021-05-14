package io.github.ocelot.sonar.item;

import io.github.ocelot.sonar.network.STestPlayMessage;
import io.github.ocelot.sonar.network.TestMessageHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        if (!world.isClientSide())
            TestMessageHandler.PLAY.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new STestPlayMessage());
        return ActionResult.success(player.getItemInHand(hand));
    }
}
