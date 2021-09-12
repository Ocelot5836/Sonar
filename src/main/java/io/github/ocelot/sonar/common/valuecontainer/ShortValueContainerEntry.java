package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports short data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 * @deprecated TODO remove in 7.0.0
 */
public class ShortValueContainerEntry extends NumberValueContainerEntry<Short>
{
    private final Component displayName;
    private boolean bounds;

    public ShortValueContainerEntry(Component displayName, String name, short value)
    {
        this(displayName, name, value, Short.MIN_VALUE, Short.MAX_VALUE);
        this.bounds = false;
    }

    public ShortValueContainerEntry(Component displayName, String name, short value, short minValue, short maxValue)
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
    public Component getDisplayName()
    {
        return displayName;
    }

    @Override
    public void write(CompoundTag nbt)
    {
        nbt.putShort(this.getName(), this.value);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_SHORT) ? nbt.getShort(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return false;
    }
}
