package io.github.ocelot.common.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <p>A spawn egg that allows for deferred entity types.</p>
 *
 * @author Ocelot
 * @since 2.8.0
 */
public class SpawnEggItemBase<T extends EntityType<?>> extends SpawnEggItem
{
    private final boolean addToMisc;
    private final Supplier<T> type;

    public SpawnEggItemBase(Supplier<T> type, int primaryColor, int secondaryColor, boolean addToMisc, Properties builder)
    {
        super(null, primaryColor, secondaryColor, builder);
        this.type = type;
        this.addToMisc = addToMisc;
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, EventPriority.LOWEST, this::onEvent);
    }

    private void onEvent(RegistryEvent.Register<EntityType<?>> event)
    {
        Map<EntityType<?>, SpawnEggItem> eggs = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, this, "field_195987_b");
        if (eggs == null)
            throw new RuntimeException("Failed to inject spawns eggs");
        eggs.put(this.type.get(), this);
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
            if (items.stream().anyMatch(stack -> stack.getItem() instanceof SpawnEggItem))
            {
                Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof SpawnEggItem && "minecraft".equals(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace())).reduce((a, b) -> b);
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
    public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_)
    {
        if (p_208076_1_ != null && p_208076_1_.contains("EntityTag", 10))
        {
            CompoundNBT compoundnbt = p_208076_1_.getCompound("EntityTag");
            if (compoundnbt.contains("id", 8))
            {
                return EntityType.byKey(compoundnbt.getString("id")).orElseGet(this.type);
            }
        }

        return this.type.get();
    }
}
