package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link BlockPos} type</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class BlockPosValueContainerEntry extends AbstractVectorValueContainerEntry<BlockPos>
{
    public BlockPosValueContainerEntry(ITextComponent displayName, String name, Vec3i value)
    {
        this(displayName, name, value, null, null);
    }

    public BlockPosValueContainerEntry(ITextComponent displayName, String name, Vec3i value, @Nullable Vec3i minValue,@Nullable Vec3i maxValue)
    {
        super(displayName, name, new BlockPos(value), minValue == null ? null : new BlockPos(minValue), maxValue == null ? null : new BlockPos(maxValue), false);
    }

    @Override
    protected BlockPos create(Number x, Number y, Number z)
    {
        return new BlockPos(x.intValue(), y.intValue(), z.intValue());
    }

    @Override
    protected Number getX(BlockPos value)
    {
        return value.getX();
    }

    @Override
    protected Number getY(BlockPos value)
    {
        return value.getY();
    }

    @Override
    protected Number getZ(BlockPos value)
    {
        return value.getZ();
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        nbt.putLong(this.getName(), this.value.asLong());
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_LONG) ? BlockPos.of(nbt.getLong(this.getName())) : new BlockPos(0, 0, 0);
    }

    @Override
    public void parse(String data)
    {
        String[] tokens = String.valueOf(data).split(",");
        Number x = NumberUtils.createNumber(tokens[0].trim());
        Number y = NumberUtils.createNumber(tokens[1].trim());
        Number z = NumberUtils.createNumber(tokens[2].trim());
        this.value = this.clamp(new BlockPos(x.intValue(), y.intValue(), z.intValue()), this.getMinValue(), this.getMaxValue());
    }
}
