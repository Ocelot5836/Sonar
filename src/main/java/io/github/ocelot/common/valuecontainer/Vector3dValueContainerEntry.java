package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link Vector3d} type</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class Vector3dValueContainerEntry extends AbstractVectorValueContainerEntry<Vector3d>
{
    public Vector3dValueContainerEntry(ITextComponent displayName, String name, Vector3d value)
    {
        this(displayName, name, value, null, null);
    }

    public Vector3dValueContainerEntry(ITextComponent displayName, String name, Vector3d value, Vector3d minValue, Vector3d maxValue)
    {
        super(displayName, name, value, minValue, maxValue, true);
    }

    @Override
    protected Vector3d create(Number x, Number y, Number z)
    {
        return new Vector3d(x.doubleValue(), y.doubleValue(), z.doubleValue());
    }

    @Override
    protected Number getX(Vector3d value)
    {
        return value.getX();
    }

    @Override
    protected Number getY(Vector3d value)
    {
        return value.getY();
    }

    @Override
    protected Number getZ(Vector3d value)
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
            this.value = new Vector3d(valueNbt.getDouble("x"), valueNbt.getDouble("y"), valueNbt.getDouble("z"));
        }
        else
        {
            this.value = new Vector3d(0, 0, 0);
        }
    }

    @Override
    public void parse(String data)
    {
        String[] tokens = String.valueOf(data).split(",");
        Number x = NumberUtils.createNumber(tokens[0].trim());
        Number y = NumberUtils.createNumber(tokens[1].trim());
        Number z = NumberUtils.createNumber(tokens[2].trim());
        this.value = this.clamp(new Vector3d(x.doubleValue(), y.doubleValue(), z.doubleValue()), this.getMinValue(), this.getMaxValue());
    }
}
