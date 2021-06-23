package io.github.ocelot.sonar.item;

import io.github.ocelot.sonar.network.STestPlayMessage;
import io.github.ocelot.sonar.network.TestMessageHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrashItem extends Item
{
    public CrashItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        if (!world.isClientSide())
            TestMessageHandler.PLAY.sendTo((ServerPlayer) player, new STestPlayMessage());
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
