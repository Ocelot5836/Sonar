package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports short data types.</p>
 *
 * @author Ocelot
 */
public class ShortValueContainerEntry extends NumberValueContainerEntry<Short>
{
    private final ITextComponent displayName;
    private boolean bounds;

    public ShortValueContainerEntry(ITextComponent displayName, String name, short value)
    {
        this(displayName, name, value, Short.MIN_VALUE, Short.MAX_VALUE);
        this.bounds = false;
    }

    public ShortValueContainerEntry(ITextComponent displayName, String name, short value, short minValue, short maxValue)
    {
        super(name, value, minValue, maxValue);
        this.displayName = displayName;
        this.bounds = true;
    }

    @Override
    protected Short getValue(Number number)
    {
        return number.shortValue();
    }

    @Override
    protected Short clamp(Short value, Short minValue, Short maxValue)
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
        nbt.putShort(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_SHORT) ? nbt.getShort(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return false;
    }
}
