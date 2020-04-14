package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports int data types.</p>
 *
 * @author Ocelot
 */
public class IntValueContainerEntry extends NumberValueContainerEntry<Integer>
{
    private final ITextComponent displayName;
    private boolean bounds;

    public IntValueContainerEntry(ITextComponent displayName, String name, int value)
    {
        this(displayName, name, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.bounds = false;
    }

    public IntValueContainerEntry(ITextComponent displayName, String name, int value, int minValue, int maxValue)
    {
        super(name, value, minValue, maxValue);
        this.displayName = displayName;
        this.bounds = true;
    }

    @Override
    protected Integer getValue(Number number)
    {
        return number.intValue();
    }

    @Override
    protected Integer clamp(Integer value, Integer minValue, Integer maxValue)
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
        nbt.putInt(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_INT) ? nbt.getInt(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return false;
    }
}
