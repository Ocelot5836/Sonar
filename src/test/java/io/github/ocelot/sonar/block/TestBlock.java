package io.github.ocelot.sonar.block;

import io.github.ocelot.sonar.common.util.Scheduler;
import io.github.ocelot.sonar.common.util.VoxelShapeHelper;
import io.github.ocelot.sonar.tileentity.TestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class TestBlock extends Block implements SimpleWaterloggedBlock
{
    private static final VoxelShape SHAPE = new VoxelShapeHelper.Builder().append(Block.box(4, 0, 4, 12, 8, 12), Block.box(5, 8, 5, 11, 16, 11)).rotate(Direction.NORTH).build();

    public TestBlock(Block.Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        Scheduler.get(world).schedule(() -> System.out.println("Hello 2 seconds after right click on " + (world.isClientSide() ? "Client" : "Server")), 2, TimeUnit.SECONDS);
        return InteractionResult.SUCCESS;
    }

    @Deprecated
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world)
    {
        return new TestTileEntity();
    }
}
