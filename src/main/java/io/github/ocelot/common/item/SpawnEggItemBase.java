package io.github.ocelot.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>A spawn egg that allows for deferred entity types.</p>
 *
 * @author Ocelot
 * @since 2.8.0
 */
public class SpawnEggItemBase<T extends Entity> extends SpawnEggItem
{
    private final boolean addToMisc;
    private final RegistryObject<EntityType<T>> type;

    public SpawnEggItemBase(RegistryObject<EntityType<T>> type, int primaryColor, int secondaryColor, boolean addToMisc, Properties builder)
    {
        super(EntityType.OCELOT, primaryColor, secondaryColor, builder);
        this.type = type;
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
            Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof SpawnEggItem && "minecraft".equals(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace())).reduce((a, b) -> b);
            if (optional.isPresent() && items.contains(optional.get()))
            {
                items.add(items.indexOf(optional.get()) + 1, new ItemStack(this));
            }
            else
            {
                items.add(new ItemStack(this));
            }
        }
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_)
    {
        if (p_208076_1_ != null && p_208076_1_.contains("EntityTag", 10))
        {
            CompoundNBT compoundnbt = p_208076_1_.getCompound("EntityTag");
            if (compoundnbt.contains("id", 8))
            {
                return EntityType.byKey(compoundnbt.getString("id")).orElse(this.type.get());
            }
        }

        return this.type.get();
    }
}
