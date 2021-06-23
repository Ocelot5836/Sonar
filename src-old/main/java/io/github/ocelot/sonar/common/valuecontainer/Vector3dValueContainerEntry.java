package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link Vec3} type</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class Vector3dValueContainerEntry extends AbstractVectorValueContainerEntry<Vec3>
{
    public Vector3dValueContainerEntry(Component displayName, String name, Vec3 value)
    {
        this(displayName, name, value, null, null);
    }

    public Vector3dValueContainerEntry(Component displayName, String name, Vec3 value, Vec3 minValue, Vec3 maxValue)
    {
        super(displayName, name, value, minValue, maxValue, true);
    }

    @Override
    protected Vec3 create(Number x, Number y, Number z)
    {
        return new Vec3(x.doubleValue(), y.doubleValue(), z.doubleValue());
    }

    @Override
    protected Number getX(Vec3 value)
    {
        return value.x();
    }

    @Override
    protected Number getY(Vec3 value)
    {
        return value.y();
    }

    @Override
    protected Number getZ(Vec3 value)
    {
        return value.z();
    }

    @Override
    public void write(CompoundTag nbt)
    {
        CompoundTag valueNbt = new CompoundTag();
        valueNbt.putDouble("x", this.value.x());
        valueNbt.putDouble("y", this.value.y());
        valueNbt.putDouble("z", this.value.z());
        nbt.put(this.getName(), valueNbt);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        if (nbt.contains(this.getName(), Constants.NBT.TAG_COMPOUND))
        {
            CompoundTag valueNbt = nbt.getCompound(this.getName());
            this.value = new Vec3(valueNbt.getDouble("x"), valueNbt.getDouble("y"), valueNbt.getDouble("z"));
        }
        else
        {
            this.value = new Vec3(0, 0, 0);
        }
    }

    @Override
    public void parse(String data)
    {
        String[] tokens = String.valueOf(data).split(",");
        Number x = NumberUtils.createNumber(tokens[0].trim());
        Number y = NumberUtils.createNumber(tokens[1].trim());
        Number z = NumberUtils.createNumber(tokens[2].trim());
        this.value = this.clamp(new Vec3(x.doubleValue(), y.doubleValue(), z.doubleValue()), this.getMinValue(), this.getMaxValue());
    }
}
