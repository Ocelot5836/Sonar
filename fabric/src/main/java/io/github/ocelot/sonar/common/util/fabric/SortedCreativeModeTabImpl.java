package io.github.ocelot.sonar.common.util.fabric;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SortedCreativeModeTabImpl
{
    public static int getNextId()
    {
        ((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
        return CreativeModeTab.TABS.length - 1;
    }
}
