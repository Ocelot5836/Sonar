package io.github.ocelot.sonar.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishBucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <p>A fish bucket that allows all entity types.</p>
 *
 * @author Ocelot
 * @since 5.0.0
 */
public class FishBucketItemBase extends FishBucketItem
{
    private final Supplier<? extends EntityType<?>> entityType;
    private final boolean addToMisc;

    public FishBucketItemBase(Supplier<? extends EntityType<?>> entityType, Fluid fluid, boolean addToMisc, Properties builder)
    {
        super(null, fluid, builder);
        this.entityType = entityType;
        this.addToMisc = addToMisc;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
    {
        if (!this.addToMisc)
        {
            super.fillItemCategory(group, items);
            return;
        }

        if (this.allowdedIn(group) || group == CreativeModeTab.TAB_MISC)
        {
            if (items.stream().anyMatch(stack -> stack.getItem() instanceof FishBucketItem))
            {
                Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof FishBucketItem && "minecraft".equals(Registry.ITEM.getKey(stack.getItem()).getNamespace())).reduce((a, b) -> b);
                if (optional.isPresent() && items.contains(optional.get()))
                {
                    items.add(items.indexOf(optional.get()) + 1, new ItemStack(this));
                    return;
                }
            }
            items.add(new ItemStack(this));
        }
    }

    @Override
    public void checkExtraContent(Level world, ItemStack stack, BlockPos pos)
    {
        if (!world.isClientSide())
        {
            this.spawn((ServerLevel) world, stack, pos);
        }
    }

    protected void spawn(ServerLevel world, ItemStack stack, BlockPos pos)
    {
        this.entityType.get().spawn(world, stack, null, pos, MobSpawnType.BUCKET, true, false);
    }
}
