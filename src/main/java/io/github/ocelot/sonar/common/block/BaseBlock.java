package io.github.ocelot.sonar.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * <p>Adds common functionality to blocks that use waterlogging or facing properties. To properly be able to waterlog a block, implement {@link SimpleWaterloggedBlock} on the implementation.</p>
 *
 * @author Ocelot
 * @since 2.3.0
 * @deprecated TODO remove in 7.0.0
 */
public class BaseBlock extends Block
{
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BaseBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos)
    {
        return getComparatorInputOverride(world.getBlockEntity(pos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
    {
        if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED))
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = this.defaultBlockState();
        if (state.hasProperty(HORIZONTAL_FACING))
            state = state.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
        if (state.hasProperty(FACING))
            state = state.setValue(FACING, context.getNearestLookingDirection().getOpposite());
        if (state.hasProperty(WATERLOGGED))
            state = state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        if (state.hasProperty(HORIZONTAL_FACING))
            state = state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
        if (state.hasProperty(FACING))
            state = state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.hasProperty(HORIZONTAL_FACING))
            state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
        if (state.hasProperty(FACING))
            state.rotate(mirror.getRotation(state.getValue(FACING)));
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<Component> getTitle(Level world, BlockPos pos)
    {
        return Optional.of(this.getName());
    }

    /**
     * Calculates the comparator redstone value for the specified tile entity for the inventory items.
     *
     * @param te The tile entity to get the override for
     * @return The redstone level output for that tile entity
     */
    public static int getComparatorInputOverride(@Nullable BlockEntity te)
    {
        if (te == null)
            return 0;

        LazyOptional<IItemHandler> itemCapability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (itemCapability.isPresent())
        {
            IItemHandler inventory = itemCapability.orElseThrow(() -> new NullPointerException("Inventory Capability was null when present!"));
            boolean empty = true;
            float fillPercentage = 0.0F;

            for (int j = 0; j < inventory.getSlots(); ++j)
            {
                ItemStack itemstack = inventory.getStackInSlot(j);
                if (!itemstack.isEmpty())
                {
                    fillPercentage += (float) itemstack.getCount() / (float) Math.min(inventory.getSlotLimit(j), itemstack.getMaxStackSize());
                    empty = false;
                }
            }

            return Mth.floor((fillPercentage / (float) inventory.getSlots()) * 14.0F) + (!empty ? 1 : 0);
        }
        return te instanceof Container ? AbstractContainerMenu.getRedstoneSignalFromContainer((Container) te) : 0;
    }
}
