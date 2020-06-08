package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link BlockPos} type</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
@SuppressWarnings("unused")
public class BlockPosValueContainerEntry extends AbstractVectorValueContainerEntry<BlockPos>
{
    public BlockPosValueContainerEntry(ITextComponent displayName, String name, Vec3i value)
    {
        this(displayName, name, value, null, null);
    }

    public BlockPosValueContainerEntry(ITextComponent displayName, String name, Vec3i value, Vec3i minValue, Vec3i maxValue)
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
        nbt.putLong(this.getName(), this.value.toLong());
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_LONG) ? BlockPos.fromLong(nbt.getLong(this.getName())) : new BlockPos(0, 0, 0);
    }

    @Override
    public void parse(Object data)
    {
        if (data instanceof BlockPos)
        {
            this.value = this.clamp((BlockPos) data, this.getMinValue(), this.getMaxValue());
            return;
        }
        if (data instanceof Vec3i)
        {
            this.value = this.clamp(new BlockPos((Vec3i) data), this.getMinValue(), this.getMaxValue());
            return;
        }
        String[] tokens = String.valueOf(data).split(",");
        if(tokens.length != 3)
            return;
        Number x = StringUtils.isEmpty(tokens[0]) ? 0 : NumberUtils.createNumber(tokens[0]);
        Number y = StringUtils.isEmpty(tokens[1]) ? 0 : NumberUtils.createNumber(tokens[1]);
        Number z = StringUtils.isEmpty(tokens[2]) ? 0 : NumberUtils.createNumber(tokens[2]);
        this.value = this.clamp(new BlockPos(x.intValue(), y.intValue(), z.intValue()), this.getMinValue(), this.getMaxValue());
    }

    @Override
    public boolean isValid(Object data)
    {
        return data instanceof Vec3i || data instanceof String;
    }

}
