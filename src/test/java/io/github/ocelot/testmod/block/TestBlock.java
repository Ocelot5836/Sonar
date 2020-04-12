package io.github.ocelot.testmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestBlock extends Block
{
    public TestBlock(Block.Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return new Builder().append(Block.makeCuboidShape(4, 0, 4, 12, 8, 12), Block.makeCuboidShape(5, 8, 5, 11, 16, 11)).build();
    }

    private static class Builder
    {
        private Set<VoxelShape> shapes;

        public Builder()
        {
            this.shapes = new HashSet<>();
        }

        public Builder append(VoxelShape... shapes)
        {
            this.shapes.addAll(Arrays.asList(shapes));
            return this;
        }

        public VoxelShape build()
        {
            return this.build(IBooleanFunction.OR);
        }

        public VoxelShape build(IBooleanFunction combineFunction)
        {
            VoxelShape result = VoxelShapes.empty();
            for (VoxelShape shape : this.shapes)
            {
                result = VoxelShapes.combine(result, shape, combineFunction);
            }
            return result.simplify();
        }
    }
}
