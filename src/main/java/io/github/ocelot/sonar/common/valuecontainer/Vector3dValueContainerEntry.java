package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link Vec3d} type</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class Vector3dValueContainerEntry extends AbstractVectorValueContainerEntry<Vec3d>
{
    public Vector3dValueContainerEntry(ITextComponent displayName, String name, Vec3d value)
    {
        this(displayName, name, value, null, null);
    }

    public Vector3dValueContainerEntry(ITextComponent displayName, String name, Vec3d value, @Nullable Vec3d minValue,@Nullable Vec3d maxValue)
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
        return value.x();
    }

    @Override
    protected Number getY(Vec3d value)
    {
        return value.y();
    }

    @Override
    protected Number getZ(Vec3d value)
    {
        return value.z();
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        CompoundNBT valueNbt = new CompoundNBT();
        valueNbt.putDouble("x", this.value.x());
        valueNbt.putDouble("y", this.value.y());
        valueNbt.putDouble("z", this.value.z());
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
