package io.github.ocelot.testmod.block;

import io.github.ocelot.common.BaseBlock;
import io.github.ocelot.common.VoxelShapeHelper;
import io.github.ocelot.testmod.tileentity.TestTileEntity;
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

public class TestStateBlock extends BaseBlock implements IWaterLoggable
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
}
