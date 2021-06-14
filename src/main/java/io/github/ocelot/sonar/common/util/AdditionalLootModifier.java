package io.github.ocelot.sonar.common.util;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p>Inserts the items from an additional loot table into a loot table.</p>
 *
 * @author Ocelot
 * @since 6.2.0
 */
public class AdditionalLootModifier extends LootModifier
{
    private final ResourceLocation lootTable;

    public AdditionalLootModifier(LootItemCondition[] conditionsIn, ResourceLocation lootTable)
    {
        super(conditionsIn);
        this.lootTable = lootTable;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext ctx)
    {
        generatedLoot.addAll(ctx.getLootTable(this.lootTable).getRandomItems(ctx));
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AdditionalLootModifier>
    {
        @Override
        public AdditionalLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditions)
        {
            ResourceLocation lootTable = new ResourceLocation(GsonHelper.getAsString(object, "lootTable"));
            return new AdditionalLootModifier(conditions, lootTable);
        }

        @Override
        public JsonObject write(AdditionalLootModifier modifier)
        {
            JsonObject object = new JsonObject();
            object.addProperty("lootTable", modifier.lootTable.toString());
            return object;
        }
    }
}
