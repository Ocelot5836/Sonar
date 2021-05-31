package io.github.ocelot.sonar.tileentity;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.common.tileentity.BaseTileEntity;
import io.github.ocelot.sonar.common.valuecontainer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TestTileEntity extends BaseTileEntity implements ValueContainer
{
    public TestTileEntity()
    {
        super(TestMod.TEST_TILE_ENTITY.get());
    }

    @Override
    public void getEntries(Level world, BlockPos pos, List<ValueContainerEntry<?>> entries)
    {
        for (int i = 0; i < 1024; i++)
        {
            entries.add(new StringValueContainerEntry(new TextComponent("lul"), Integer.toString(i), "Epic Value btw"));
            entries.add(new FloatValueContainerEntry(new TextComponent("Float " + i), "test" + i, i));
            entries.add(new BooleanValueContainerEntry(new TextComponent("Boolean " + i), "bool" + i, i % 2 == 0));
            entries.add(new ResourceLocationValueContainerEntry(new TextComponent("Resource Location " + i), "resourceLocation" + i, BuiltInLootTables.EMPTY));
            entries.add(new RegistryObjectValueContainerEntry<>(new TextComponent("Enchantment " + i), "enchantment" + i, ForgeRegistries.ENCHANTMENTS, Enchantments.BINDING_CURSE));
            entries.add(new RegistryObjectValueContainerEntry<>(new TextComponent("Block " + i), "block" + i, ForgeRegistries.BLOCKS, Blocks.ACACIA_PLANKS));
            entries.add(new RegistryObjectValueContainerEntry<>(new TextComponent("Sound Event " + i), "soundevent" + i, ForgeRegistries.SOUND_EVENTS, SoundEvents.BEEHIVE_DRIP));
            entries.add(new ArrayValueContainerEntry<>(new TextComponent("Array " + i), "array" + i, ValueContainerEntry.InputType.values()).setDisplayGenerator(side -> side.name().toLowerCase(Locale.ROOT)));
        }
    }

    @Override
    public void readEntries(Level world, BlockPos pos, Map<String, ValueContainerEntry<?>> entries)
    {
        System.out.println(entries);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<Component> getTitle(Level world, BlockPos pos)
    {
        return Optional.of(world.getBlockState(pos).getBlock().getName());
    }
}
