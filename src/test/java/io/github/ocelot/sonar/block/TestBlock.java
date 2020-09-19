package io.github.ocelot.sonar.block;

import io.github.ocelot.sonar.common.BaseBlock;
import io.github.ocelot.sonar.common.VoxelShapeHelper;
import io.github.ocelot.sonar.tileentity.TestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class TestBlock extends BaseBlock implements IWaterLoggable
{
    private static final VoxelShape SHAPE = new VoxelShapeHelper.Builder().append(Block.makeCuboidShape(4, 0, 4, 12, 8, 12), Block.makeCuboidShape(5, 8, 5, 11, 16, 11)).rotate(Direction.NORTH).build();

    public TestBlock(Block.Properties properties)
    {
        super(properties);
    }

    @Deprecated
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TestTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }
}
