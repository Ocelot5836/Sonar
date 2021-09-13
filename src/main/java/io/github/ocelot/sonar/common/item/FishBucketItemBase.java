package io.github.ocelot.sonar.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
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
public class FishBucketItemBase extends MobBucketItem
{
    private final boolean addToMisc;

    public FishBucketItemBase(Supplier<? extends EntityType<?>> entityType, Supplier<? extends Fluid> fluid, Supplier<? extends SoundEvent> soundSupplier, boolean addToMisc, Properties builder)
    {
        super(entityType, fluid, soundSupplier, builder);
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
            if (items.stream().anyMatch(stack -> stack.getItem() instanceof MobBucketItem))
            {
                Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof MobBucketItem && "minecraft".equals(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace())).reduce((a, b) -> b);
                if (optional.isPresent() && items.contains(optional.get()))
                {
                    items.add(items.indexOf(optional.get()) + 1, new ItemStack(this));
                    return;
                }
            }
            items.add(new ItemStack(this));
        }
    }
}
