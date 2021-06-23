package io.github.ocelot.sonar.common.block.forge;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class BaseBlockImpl
{
    public static int getComparatorInputOverride(@Nullable BlockEntity te)
    {
        if (te == null)
            return 0;

        LazyOptional<IItemHandler> itemCapability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (itemCapability.isPresent())
        {
            IItemHandler inventory = itemCapability.orElseThrow(() -> new NullPointerException("Inventory Capability was null when present!"));
            boolean empty = true;
            float fillPercentage = 0.0F;

            for (int j = 0; j < inventory.getSlots(); ++j)
            {
                ItemStack itemstack = inventory.getStackInSlot(j);
                if (!itemstack.isEmpty())
                {
                    fillPercentage += (float) itemstack.getCount() / (float) Math.min(inventory.getSlotLimit(j), itemstack.getMaxStackSize());
                    empty = false;
                }
            }

            return Mth.floor((fillPercentage / (float) inventory.getSlots()) * 14.0F) + (!empty ? 1 : 0);
        }
        return te instanceof Container ? AbstractContainerMenu.getRedstoneSignalFromContainer((Container) te) : 0;
    }
}
