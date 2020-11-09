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
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
    {
        return getComparatorInputOverride(world.getTileEntity(pos));
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        if (state.has(WATERLOGGED) && state.get(WATERLOGGED))
            world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = this.getDefaultState();
        if (state.has(HORIZONTAL_FACING))
            state = state.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
        if (state.has(FACING))
            state = state.with(FACING, context.getNearestLookingDirection().getOpposite());
        if (state.has(WATERLOGGED))
            state = state.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        if (state.has(HORIZONTAL_FACING))
            state = state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
        if (state.has(FACING))
            state = state.with(FACING, rotation.rotate(state.get(FACING)));
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.has(HORIZONTAL_FACING))
            state.rotate(mirror.toRotation(state.get(HORIZONTAL_FACING)));
        if (state.has(FACING))
            state.rotate(mirror.toRotation(state.get(FACING)));
        return state;
    }

    @Override
    public IFluidState getFluidState(BlockState state)
    {
        return state.has(WATERLOGGED) && state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<ITextComponent> getTitle(World world, BlockPos pos)
    {
        return Optional.of(this.getNameTextComponent());
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
        return te instanceof IInventory ? Container.calcRedstoneFromInventory((IInventory) te) : 0;
    }
}
