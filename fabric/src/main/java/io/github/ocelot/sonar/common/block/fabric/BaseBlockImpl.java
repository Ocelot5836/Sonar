package io.github.ocelot.sonar.common.block.fabric;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class BaseBlockImpl
{
    public static int getComparatorInputOverride(@Nullable BlockEntity te)
    {
        return te instanceof Container ? AbstractContainerMenu.getRedstoneSignalFromContainer((Container) te) : 0;
    }
}
