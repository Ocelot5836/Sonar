package io.github.ocelot.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
        TileEntity te = world.getTileEntity(pos);
        if (te != null)
        {
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
        }
        if (world.getTileEntity(pos) instanceof IInventory)
        {
            return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(pos));
        }
        return 0;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te != null)
            {
                LazyOptional<IItemHandler> itemCapability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if (itemCapability.isPresent())
                {
                    IItemHandler inventory = itemCapability.orElseThrow(() -> new NullPointerException("Inventory Capability was null when present!"));
                    for (int i = 0; i < inventory.getSlots(); i++)
                    {
                        InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory.getStackInSlot(i));
                    }
                    world.updateComparatorOutputLevel(pos, this);
                }
            }
            if (te instanceof IInventory)
            {
                InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
                world.updateComparatorOutputLevel(pos, this);
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = this.getDefaultState();
        if (state.hasProperty(HORIZONTAL_FACING))
        {
            state = state.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
        }
        if (state.hasProperty(FACING))
        {
            state = state.with(FACING, context.getNearestLookingDirection().getOpposite());
        }
        if (state.hasProperty(WATERLOGGED))
        {
            state = state.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        if (state.hasProperty(HORIZONTAL_FACING))
        {
            state = state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
        }
        if (state.hasProperty(FACING))
        {
            state = state.with(FACING, rotation.rotate(state.get(FACING)));
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.hasProperty(HORIZONTAL_FACING))
        {
            state.rotate(mirror.toRotation(state.get(HORIZONTAL_FACING)));
        }
        if (state.hasProperty(FACING))
        {
            state.rotate(mirror.toRotation(state.get(FACING)));
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.hasProperty(WATERLOGGED) && state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}
