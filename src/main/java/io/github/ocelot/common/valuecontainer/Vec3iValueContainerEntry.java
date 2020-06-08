package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link Vec3i} type</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
@SuppressWarnings("unused")
public class Vec3iValueContainerEntry extends AbstractVectorValueContainerEntry<Vec3i>
{
    public Vec3iValueContainerEntry(ITextComponent displayName, String name, Vec3i value)
    {
        this(displayName, name, value, null, null);
    }

    public Vec3iValueContainerEntry(ITextComponent displayName, String name, Vec3i value, Vec3i minValue, Vec3i maxValue)
    {
        super(displayName, name, value, minValue, maxValue, false);
    }

    @Override
    protected Vec3i create(Number x, Number y, Number z)
    {
        return new Vec3i(x.intValue(), y.intValue(), z.intValue());
    }

    @Override
    protected Number getX(Vec3i value)
    {
        return value.getX();
    }

    @Override
    protected Number getY(Vec3i value)
    {
        return value.getY();
    }

    @Override
    protected Number getZ(Vec3i value)
    {
        return value.getZ();
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        CompoundNBT valueNbt = new CompoundNBT();
        valueNbt.putInt("x", this.value.getX());
        valueNbt.putInt("y", this.value.getY());
        valueNbt.putInt("z", this.value.getZ());
        nbt.put(this.getName(), valueNbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        if (nbt.contains(this.getName(), Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT valueNbt = nbt.getCompound(this.getName());
            this.value = new Vec3i(valueNbt.getInt("x"), valueNbt.getInt("y"), valueNbt.getInt("z"));
        }
        else
        {
            this.value = new Vec3i(0, 0, 0);
        }
    }

    @Override
    public void parse(Object data)
    {
        if (data instanceof Vec3i)
        {
            this.value = this.clamp((Vec3i) data, this.getMinValue(), this.getMaxValue());
            return;
        }
        String[] tokens = String.valueOf(data).split(",");
        if(tokens.length != 3)
            return;
        Number x = StringUtils.isEmpty(tokens[0]) ? 0 : NumberUtils.createNumber(tokens[0]);
        Number y = StringUtils.isEmpty(tokens[1]) ? 0 : NumberUtils.createNumber(tokens[1]);
        Number z = StringUtils.isEmpty(tokens[2]) ? 0 : NumberUtils.createNumber(tokens[2]);
        this.value = this.clamp(new Vec3i(x.intValue(), y.intValue(), z.intValue()), this.getMinValue(), this.getMaxValue());
    }

    @Override
    public boolean isValid(Object data)
    {
        return data instanceof Vec3i || data instanceof String;
    }

}
