package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports byte data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 * @deprecated TODO remove in 7.0.0
 */
public class ByteValueContainerEntry extends NumberValueContainerEntry<Byte>
{
    private final Component displayName;
    private boolean bounds;

    public ByteValueContainerEntry(Component displayName, String name, byte value)
    {
        this(displayName, name, value, Byte.MIN_VALUE, Byte.MAX_VALUE);
        this.bounds = false;
    }

    public ByteValueContainerEntry(Component displayName, String name, byte value, byte minValue, byte maxValue)
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
    public Component getDisplayName()
    {
        return displayName;
    }

    @Override
    public void write(CompoundTag nbt)
    {
        nbt.putByte(this.getName(), this.value);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_BYTE) ? nbt.getByte(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return false;
    }
}
