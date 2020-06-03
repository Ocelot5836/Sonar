package io.github.ocelot.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

/**
 * <p>A spawn egg that allows for deferred entity types.</p>
 *
 * @author Ocelot
 * @since 2.8.0
 */
@SuppressWarnings("unused")
public class SpawnEggItemBase<T extends Entity> extends SpawnEggItem
{
    private final RegistryObject<EntityType<T>> type;

    public SpawnEggItemBase(RegistryObject<EntityType<T>> type, int primaryColor, int secondaryColor, Properties builder)
    {
        super(EntityType.OCELOT, primaryColor, secondaryColor, builder);
        this.type = type;
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
