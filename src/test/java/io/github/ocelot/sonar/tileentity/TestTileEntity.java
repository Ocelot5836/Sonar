package io.github.ocelot.sonar.tileentity;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.common.tileentity.BaseTileEntity;
import io.github.ocelot.sonar.common.valuecontainer.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class TestTileEntity extends BaseTileEntity implements ValueContainer
{
    public TestTileEntity()
    {
        super(TestMod.TEST_TILE_ENTITY.get());
    }

    @Override
    public void getEntries(World world, BlockPos pos, List<ValueContainerEntry<?>> entries)
    {
        for (int i = 0; i < 1024; i++)
        {
            entries.add(new StringValueContainerEntry(new StringTextComponent(EnchantmentNameParts.getInstance().getRandomName(Objects.requireNonNull(Minecraft.getInstance().getFontManager().get(Minecraft.ALT_FONT)), 64)), Integer.toString(i), "Epic Value btw"));
            entries.add(new FloatValueContainerEntry(new StringTextComponent("Float " + i), "test" + i, i));
            entries.add(new BooleanValueContainerEntry(new StringTextComponent("Boolean " + i), "bool" + i, i % 2 == 0));
            entries.add(new ResourceLocationValueContainerEntry(new StringTextComponent("Resource Location " + i), "resourceLocation" + i, LootTables.EMPTY));
            entries.add(new RegistryObjectValueContainerEntry<>(new StringTextComponent("Enchantment " + i), "enchantment" + i, ForgeRegistries.ENCHANTMENTS, Enchantments.BINDING_CURSE));
            entries.add(new RegistryObjectValueContainerEntry<>(new StringTextComponent("Block " + i), "block" + i, ForgeRegistries.BLOCKS, Blocks.ACACIA_PLANKS));
            entries.add(new RegistryObjectValueContainerEntry<>(new StringTextComponent("Sound Event " + i), "soundevent" + i, ForgeRegistries.SOUND_EVENTS, SoundEvents.BEEHIVE_DRIP));
            entries.add(new ArrayValueContainerEntry<>(new StringTextComponent("Array " + i), "array" + i, ValueContainerEntry.InputType.values()).setDisplayGenerator(side -> side.name().toLowerCase(Locale.ROOT)));
        }
    }

    @Override
    public void readEntries(World world, BlockPos pos, Map<String, ValueContainerEntry<?>> entries)
    {
        System.out.println(entries);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<ITextComponent> getTitle(World world, BlockPos pos)
    {
        return Optional.empty();
    }
}
