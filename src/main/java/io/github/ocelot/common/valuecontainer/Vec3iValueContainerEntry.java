package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link Vector3i} type</p>
 *
 * @author Ocelot
 * @since 3.1.0
 * TODO rename to Vector3iValueContainerEntry in 4.0.0
 */
@SuppressWarnings("unused")
public class Vec3iValueContainerEntry extends AbstractVectorValueContainerEntry<Vector3i>
{
    public Vec3iValueContainerEntry(ITextComponent displayName, String name, Vector3i value)
    {
        this(displayName, name, value, null, null);
    }

    public Vec3iValueContainerEntry(ITextComponent displayName, String name, Vector3i value, Vector3i minValue, Vector3i maxValue)
    {
        super(displayName, name, value, minValue, maxValue, false);
    }

    @Override
    protected Vector3i create(Number x, Number y, Number z)
    {
        return new Vector3i(x.intValue(), y.intValue(), z.intValue());
    }

    @Override
    protected Number getX(Vector3i value)
    {
        return value.getX();
    }

    @Override
    protected Number getY(Vector3i value)
    {
        return value.getY();
    }

    @Override
    protected Number getZ(Vector3i value)
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
            this.value = new Vector3i(valueNbt.getInt("x"), valueNbt.getInt("y"), valueNbt.getInt("z"));
        }
        else
        {
            this.value = new Vector3i(0, 0, 0);
        }
    }

    @Override
    public void parse(String data)
    {
        String[] tokens = String.valueOf(data).split(",");
        Number x = NumberUtils.createNumber(tokens[0].trim());
        Number y = NumberUtils.createNumber(tokens[1].trim());
        Number z = NumberUtils.createNumber(tokens[2].trim());
        this.value = this.clamp(new Vector3i(x.intValue(), y.intValue(), z.intValue()), this.getMinValue(), this.getMaxValue());
    }
}
