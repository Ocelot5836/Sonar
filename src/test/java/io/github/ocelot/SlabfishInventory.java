package io.github.ocelot;

import io.github.ocelot.common.DynamicInventory;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.HashSet;
import java.util.Set;

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
