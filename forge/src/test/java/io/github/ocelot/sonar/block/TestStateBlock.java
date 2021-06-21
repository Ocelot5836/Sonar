package io.github.ocelot.sonar.block;

import io.github.ocelot.sonar.common.block.BaseBlock;
import io.github.ocelot.sonar.common.valuecontainer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestStateBlock extends BaseBlock implements ValueContainer
{
    public TestStateBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public void getEntries(Level world, BlockPos pos, List<ValueContainerEntry<?>> entries)
    {
        entries.add(new Vector3dValueContainerEntry(new TextComponent("test"), "test", new Vec3(0, 1, 0)));
        entries.add(new Vector3iValueContainerEntry(new TextComponent("test"), "test", new Vec3i(0, 1, 0)));
        entries.add(new BlockPosValueContainerEntry(new TextComponent("test"), "test", new Vec3i(0, 1, 0)));
        entries.add(new IntValueContainerEntry(new TextComponent("test"), "test", 1, 0, 10));
    }

    @Override
    public void readEntries(Level world, BlockPos pos, Map<String, ValueContainerEntry<?>> entries)
    {
    }

    @Nullable
    @Override
    public CompoundTag writeClientValueContainer(Level world, BlockPos pos)
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Test", 7);
        return nbt;
    }

    @Override
    public void readClientValueContainer(Level world, BlockPos pos, CompoundTag nbt)
    {
        System.out.println("Received " + nbt.getInt("Test"));
    }
}
