package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports byte data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class ByteValueContainerEntry extends NumberValueContainerEntry<Byte>
{
    private final ITextComponent displayName;
    private boolean bounds;

    public ByteValueContainerEntry(ITextComponent displayName, String name, byte value)
    {
        this(displayName, name, value, Byte.MIN_VALUE, Byte.MAX_VALUE);
        this.bounds = false;
    }

    public ByteValueContainerEntry(ITextComponent displayName, String name, byte value, byte minValue, byte maxValue)
    {
        super(name, value, minValue, maxValue);
        this.displayName = displayName;
        this.bounds = true;
    }

    @Override
    protected Byte getValue(Number number)
    {
        return number.byteValue();
    }

    @Override
    protected Byte clamp(Byte value, Byte minValue, Byte maxValue)
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
        nbt.putByte(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_BYTE) ? nbt.getByte(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return false;
    }
}
