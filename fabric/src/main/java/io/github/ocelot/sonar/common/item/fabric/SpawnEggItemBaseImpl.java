package io.github.ocelot.sonar.common.item.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class SpawnEggItemBaseImpl
{
    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS;

    static
    {
        try
        {
            Field f = SpawnEggItem.class.getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", SpawnEggItem.class.getName(), "field_8914", "Lnet/minecraft/world/item/SpawnEggItem;BY_ID:Ljava/util/Map;"));
            f.setAccessible(true);
            SPAWN_EGGS = (Map<EntityType<?>, SpawnEggItem>) f.get(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T extends EntityType<?>> void injectSpawnEgg(Supplier<T> type, SpawnEggItem item)
    {
        if (SPAWN_EGGS == null)
            throw new RuntimeException("Failed to inject spawns eggs");
        SPAWN_EGGS.put(type.get(), item);
    }
}
