package io.github.ocelot.sonar.common.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FishBucketItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
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
public class FishBucketItemBase extends FishBucketItem implements ISortInTab
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
            super.fillItemGroup(group, items);
        else
            ISortInTab.super.fillItemGroup(group, items);
    }

    @Override
    public boolean isType(ItemStack stack)
    {
        return stack.getItem() instanceof FishBucketItem && "minecraft".equals(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace());
    }

    @Override
    public ItemGroup getItemGroup()
    {
        return ItemGroup.MISC;
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
