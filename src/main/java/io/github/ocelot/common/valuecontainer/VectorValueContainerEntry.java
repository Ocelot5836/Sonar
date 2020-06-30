package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link Vec3d} type</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public class VectorValueContainerEntry extends AbstractVectorValueContainerEntry<Vec3d>
{
    public VectorValueContainerEntry(ITextComponent displayName, String name, Vec3d value)
    {
        this(displayName, name, value, null, null);
    }

    public VectorValueContainerEntry(ITextComponent displayName, String name, Vec3d value, Vec3d minValue, Vec3d maxValue)
    {
        super(displayName, name, value, minValue, maxValue, true);
    }

    @Override
    protected Vec3d create(Number x, Number y, Number z)
    {
        return new Vec3d(x.doubleValue(), y.doubleValue(), z.doubleValue());
    }

    @Override
    protected Number getX(Vec3d value)
    {
        return value.getX();
    }

    @Override
    protected Number getY(Vec3d value)
    {
        return value.getY();
    }

    @Override
    protected Number getZ(Vec3d value)
    {
        return value.getZ();
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        CompoundNBT valueNbt = new CompoundNBT();
        valueNbt.putDouble("x", this.value.getX());
        valueNbt.putDouble("y", this.value.getY());
        valueNbt.putDouble("z", this.value.getZ());
        nbt.put(this.getName(), valueNbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        if (nbt.contains(this.getName(), Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT valueNbt = nbt.getCompound(this.getName());
            this.value = new Vec3d(valueNbt.getDouble("x"), valueNbt.getDouble("y"), valueNbt.getDouble("z"));
        }
        else
        {
            this.value = new Vec3d(0, 0, 0);
        }
    }

    @Override
    public void parse(String data)
    {
        String[] tokens = String.valueOf(data).split(",");
        Number x = NumberUtils.createNumber(tokens[0].trim());
        Number y = NumberUtils.createNumber(tokens[1].trim());
        Number z = NumberUtils.createNumber(tokens[2].trim());
        this.value = this.clamp(new Vec3d(x.doubleValue(), y.doubleValue(), z.doubleValue()), this.getMinValue(), this.getMaxValue());
    }
}
