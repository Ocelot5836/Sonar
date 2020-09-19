package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports long data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class LongValueContainerEntry extends NumberValueContainerEntry<Long>
{
    private final ITextComponent displayName;
    private boolean bounds;

    public LongValueContainerEntry(ITextComponent displayName, String name, long value)
    {
        this(displayName, name, value, Long.MIN_VALUE, Long.MAX_VALUE);
        this.bounds = false;
    }

    public LongValueContainerEntry(ITextComponent displayName, String name, long value, long minValue, long maxValue)
    {
        super(name, value, minValue, maxValue);
        this.displayName = displayName;
        this.bounds = true;
    }

    @Override
    protected Long getValue(Number number)
    {
        return number.longValue();
    }

    @Override
    protected Long clamp(Long value, Long minValue, Long maxValue)
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
        nbt.putLong(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_LONG) ? nbt.getLong(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return false;
    }
}
