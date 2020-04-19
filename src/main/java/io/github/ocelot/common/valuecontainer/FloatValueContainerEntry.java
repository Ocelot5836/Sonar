package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports float data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class FloatValueContainerEntry extends NumberValueContainerEntry<Float>
{
    private final ITextComponent displayName;
    private boolean bounds;

    public FloatValueContainerEntry(ITextComponent displayName, String name, float value)
    {
        this(displayName, name, value, Float.MIN_VALUE, Float.MAX_VALUE);
        this.bounds = false;
    }

    public FloatValueContainerEntry(ITextComponent displayName, String name, float value, float minValue, float maxValue)
    {
        super(name, value, minValue, maxValue);
        this.displayName = displayName;
        this.bounds = true;
    }

    @Override
    protected Float getValue(Number number)
    {
        return number.floatValue();
    }

    @Override
    protected Float clamp(Float value, Float minValue, Float maxValue)
    {
        if (value < minValue)
            value = minValue;
        if (value > maxValue)
            value = maxValue;
        return value;
    }

    @Override
    protected boolean hasBounds()
    {
        return bounds;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return displayName;
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        nbt.putFloat(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_FLOAT) ? nbt.getFloat(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return true;
    }
}
