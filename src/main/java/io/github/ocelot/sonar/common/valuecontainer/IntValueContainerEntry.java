package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link NumberValueContainerEntry} that supports int data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 * @deprecated TODO remove in 7.0.0
 */
public class IntValueContainerEntry extends NumberValueContainerEntry<Integer>
{
    private final Component displayName;
    private boolean bounds;

    public IntValueContainerEntry(Component displayName, String name, int value)
    {
        this(displayName, name, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.bounds = false;
    }

    public IntValueContainerEntry(Component displayName, String name, int value, int minValue, int maxValue)
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
    public Component getDisplayName()
    {
        return displayName;
    }

    @Override
    public void write(CompoundTag nbt)
    {
        nbt.putInt(this.getName(), this.value);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_INT) ? nbt.getInt(this.getName()) : this.getPreviousValue();
    }

    @Override
    public boolean isDecimal()
    {
        return false;
    }
}
