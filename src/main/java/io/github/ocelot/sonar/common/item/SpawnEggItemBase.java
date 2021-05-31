package io.github.ocelot.sonar.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.Map;
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
    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
    private final boolean addToMisc;
    private final Supplier<T> type;

    public SpawnEggItemBase(Supplier<T> type, int backgroundColor, int spotColor, boolean addToMisc, Properties builder)
    {
        super(null, backgroundColor, spotColor, builder);
        this.type = type;
        this.addToMisc = addToMisc;
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, EventPriority.LOWEST, this::onEvent);
    }

    private void onEvent(RegistryEvent.Register<EntityType<?>> event)
    {
        if (SPAWN_EGGS == null)
            throw new RuntimeException("Failed to inject spawns eggs");
        SPAWN_EGGS.put(this.type.get(), this);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
    {
        if (this.allowdedIn(group) || (this.addToMisc && group == CreativeModeTab.TAB_MISC))
        {
            if (items.stream().anyMatch(stack -> stack.getItem() instanceof SpawnEggItem))
            {
                String itemName = this.getRegistryName() == null ? null : this.getRegistryName().getPath();
                Optional<ItemStack> optional = itemName == null ? Optional.empty() : items.stream().filter(stack -> stack.getItem() instanceof SpawnEggItem).max((a, b) ->
                {
                    if (a.getItem().getRegistryName() == null || b.getItem().getRegistryName() == null)
                        return 0;
                    int valA = itemName.compareToIgnoreCase(a.getItem().getRegistryName().getPath());
                    int valB = b.getItem().getRegistryName().getPath().compareToIgnoreCase(itemName);
                    return valB - valA;
                });
                if (optional.isPresent())
                {
                    items.add(items.indexOf(optional.get()) + 1, new ItemStack(this));
                    return;
                }
            }
            items.add(new ItemStack(this));
        }
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag p_208076_1_)
    {
        if (p_208076_1_ != null && p_208076_1_.contains("EntityTag", 10))
        {
            CompoundTag compoundnbt = p_208076_1_.getCompound("EntityTag");
            if (compoundnbt.contains("id", 8))
            {
                return EntityType.byString(compoundnbt.getString("id")).orElseGet(this.type);
            }
        }

        return this.type.get();
    }
}
