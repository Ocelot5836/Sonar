package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports double data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class DoubleValueContainerEntry extends NumberValueContainerEntry<Double>
{
    private final ITextComponent displayName;
    private boolean bounds;

    public DoubleValueContainerEntry(ITextComponent displayName, String name, double value)
    {
        this(displayName, name, value, Double.MIN_VALUE, Double.MAX_VALUE);
        this.bounds = false;
    }

    public DoubleValueContainerEntry(ITextComponent displayName, String name, double value, double minValue, double maxValue)
    {
        super(name, value, minValue, maxValue);
        this.displayName = displayName;
        this.bounds = true;
    }

    @Override
    protected Double getValue(Number number)
    {
        return number.doubleValue();
    }

    @Override
    protected Double clamp(Double value, Double minValue, Double maxValue)
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
        nbt.putDouble(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_DOUBLE) ? nbt.getDouble(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return true;
    }
}
