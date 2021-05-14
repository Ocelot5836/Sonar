package io.github.ocelot.sonar.common.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * <p>Adds syncing capabilities to {@link TileEntity}. Nothing special happens, it just automatically handles .</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public class BaseTileEntity extends TileEntity
{
    public BaseTileEntity(TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
    }

    /**
     * Writes the client syncing data to NBT server side.
     *
     * @param nbt The tag to write to
     * @return The tag passed in
     */
    public CompoundNBT writeSyncTag(CompoundNBT nbt)
    {
        return this.save(nbt);
    }

    /**
     * Reads the client syncing data from NBT client side.
     *
     * @param nbt The tag to read from
     */
    @OnlyIn(Dist.CLIENT)
    public void readSyncTag(CompoundNBT nbt)
    {
        this.load(nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.readSyncTag(pkt.getTag());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.writeSyncTag(new CompoundNBT());
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<ITextComponent> getTitle(World world, BlockPos pos)
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
