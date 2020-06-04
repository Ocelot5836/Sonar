package io.github.ocelot.block;

import io.github.ocelot.common.BaseBlock;
import io.github.ocelot.common.VoxelShapeHelper;
import io.github.ocelot.common.valuecontainer.BooleanValueContainerEntry;
import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.common.valuecontainer.ValueContainerEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestStateBlock extends BaseBlock implements IWaterLoggable, ValueContainer
{
    private static final VoxelShape SHAPE = new VoxelShapeHelper.Builder().append(Block.makeCuboidShape(4, 0, 4, 12, 8, 12), Block.makeCuboidShape(5, 8, 5, 11, 16, 11)).rotate(Direction.NORTH).build();

    public TestStateBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }

    @Override
    public void getEntries(World world, BlockPos pos, List<ValueContainerEntry<?>> entries)
    {
        entries.add(new BooleanValueContainerEntry(new StringTextComponent("test"), "test", true));
    }

    @Override
    public void readEntries(World world, BlockPos pos, Map<String, ValueContainerEntry<?>> entries)
    {
    }

    @Override
    public Optional<ITextComponent> getTitle(World world, BlockPos pos)
    {
        return Optional.empty();
    }
}
