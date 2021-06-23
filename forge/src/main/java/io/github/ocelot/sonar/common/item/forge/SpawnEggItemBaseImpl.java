package io.github.ocelot.sonar.common.item.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;
import java.util.function.Supplier;

public class SpawnEggItemBaseImpl
{
    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");

    public static <T extends EntityType<?>> void injectSpawnEgg(Supplier<T> type, SpawnEggItem item)
    {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, EventPriority.LOWEST, event ->
        {
            if (SPAWN_EGGS == null)
                throw new RuntimeException("Failed to inject spawns eggs");
            SPAWN_EGGS.put(type.get(), item);
        });
    }
}
