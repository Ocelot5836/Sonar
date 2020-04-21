package io.github.ocelot.testmod.tileentity;

import io.github.ocelot.common.valuecontainer.*;
import io.github.ocelot.testmod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TestTileEntity extends TileEntity implements ValueContainer
{
    public TestTileEntity()
    {
        super(TestMod.TEST_TILE_ENTITY.get());
    }

    private void sync()
    {
        this.markDirty();
        if (this.world != null)
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.read(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    @Override
    public void getEntries(List<ValueContainerEntry<?>> entries)
    {
        for (int i = 0; i < 3; i++)
        {
            entries.add(new StringValueContainerEntry(new StringTextComponent(EnchantmentNameParts.getInstance().generateNewRandomName(Objects.requireNonNull(Minecraft.getInstance().getFontResourceManager().getFontRenderer(Minecraft.standardGalacticFontRenderer)), 64)), Integer.toString(i), "Epic Value btw"));
            entries.add(new FloatValueContainerEntry(new StringTextComponent("Float " + i), "test" + i, i));
            entries.add(new BooleanValueContainerEntry(new StringTextComponent("Boolean " + i), "bool" + i, false));
        }
    }

    @Override
    public void readEntries(Map<String, ValueContainerEntry<?>> entries)
    {
        System.out.println(entries);
    }

    @Override
    public Optional<ITextComponent> getTitle()
    {
        return Optional.of(this.getBlockState().getBlock().getNameTextComponent());
    }

    @Override
    public BlockPos getContainerPos()
    {
        return this.getPos();
    }
}
