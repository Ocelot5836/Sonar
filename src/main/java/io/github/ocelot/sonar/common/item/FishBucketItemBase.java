package io.github.ocelot.sonar.common.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FishBucketItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
    private final boolean addToMisc;

    public FishBucketItemBase(Supplier<? extends EntityType<?>> entityType, Supplier<? extends Fluid> fluid, boolean addToMisc, Properties builder)
    {
        super(entityType, fluid, builder);
        this.addToMisc = addToMisc;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (!this.addToMisc)
        {
            super.fillItemGroup(group, items);
            return;
        }

        if (this.isInGroup(group) || group == ItemGroup.MISC)
        {
            if (items.stream().anyMatch(stack -> stack.getItem() instanceof FishBucketItem))
            {
                Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof FishBucketItem && "minecraft".equals(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace())).reduce((a, b) -> b);
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
    public void onLiquidPlaced(World world, ItemStack stack, BlockPos pos)
    {
        if (!world.isRemote())
        {
            this.placeFish((ServerWorld) world, stack, pos);
        }
    }

    protected void placeFish(ServerWorld world, ItemStack stack, BlockPos pos)
    {
        this.getFishType().spawn(world, stack, null, pos, SpawnReason.BUCKET, true, false);
    }
}
