package io.github.ocelot.sonar.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * <p>Adds syncing capabilities to {@link BlockEntity}. Nothing special happens, it just automatically handles .</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public class BaseTileEntity extends BlockEntity
{
    public BaseTileEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state)
    {
        super(tileEntityType, pos, state);
    }

    /**
     * Writes the client syncing data to NBT server side.
     *
     * @param nbt The tag to write to
     * @return The tag passed in
     */
    public CompoundTag writeSyncTag(CompoundTag nbt)
    {
        return this.save(nbt);
    }

    /**
     * Reads the client syncing data from NBT client side.
     *
     * @param nbt The tag to read from
     */
    @OnlyIn(Dist.CLIENT)
    public void readSyncTag(CompoundTag nbt)
    {
        this.load(nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        this.readSyncTag(pkt.getTag());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.writeSyncTag(new CompoundTag());
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<Component> getTitle(Level world, BlockPos pos)
    {
        return Optional.of(this.getBlockState().getBlock().getName());
    }

    /**
     * Syncs tile entity data with tracking clients.
     */
    public void sync()
    {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
    }
}
