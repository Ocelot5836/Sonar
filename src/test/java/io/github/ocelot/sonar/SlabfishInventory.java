package io.github.ocelot.sonar;

import io.github.ocelot.sonar.common.util.DynamicInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SlabfishInventory extends DynamicInventory
{
    private final SlabfishEntity slabfish;

    public SlabfishInventory(SlabfishEntity slabfish)
    {
        this.slabfish = slabfish;
    }

    @Override
    public int getSizeInventory()
    {
        return 1 + (this.slabfish.hasBackpack() ? 15 : 0);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return this.slabfish.isAlive() && player.getDistanceSq(this.slabfish) <= 64.0;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index != 0 || this.slabfish.getSweaterMap().containsKey(stack.getItem());
    }

    @Override
    public int getSlotStackLimit(int index)
    {
        return index == 0 ? 1 : super.getSlotStackLimit(index);
    }
}
