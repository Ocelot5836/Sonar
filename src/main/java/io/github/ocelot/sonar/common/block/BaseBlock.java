package io.github.ocelot.sonar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.block.Block.Properties;

/**
 * <p>Adds common functionality to blocks that use waterlogging or facing properties. To properly be able to waterlog a block, implement {@link IWaterLoggable} on the implementation.</p>
 *
 * @author Ocelot
 * @since 2.3.0
 */
@SuppressWarnings("deprecation")
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
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos)
    {
        return getComparatorInputOverride(world.getBlockEntity(pos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED))
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
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
    public IFluidState getFluidState(BlockState state)
    {
        return state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<ITextComponent> getTitle(World world, BlockPos pos)
    {
        return Optional.of(this.getName());
    }

    /**
     * Calculates the comparator redstone value for the specified tile entity for the inventory items.
     *
     * @param te The tile entity to get the override for
     * @return The redstone level output for that tile entity
     */
    public static int getComparatorInputOverride(@Nullable TileEntity te)
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

            return MathHelper.floor((fillPercentage / (float) inventory.getSlots()) * 14.0F) + (!empty ? 1 : 0);
        }
        return te instanceof IInventory ? Container.getRedstoneSignalFromContainer((IInventory) te) : 0;
    }
}
