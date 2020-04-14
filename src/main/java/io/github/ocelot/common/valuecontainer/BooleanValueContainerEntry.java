package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * <p>A {@link ValueContainerEntry} that supports boolean data types.</p>
 *
 * @author Ocelot
 */
public class BooleanValueContainerEntry implements ValueContainerEntry<Boolean>, ToggleEntry
{
    private final ITextComponent displayName;
    private final String name;
    private final Boolean previousValue;
    private Boolean value;

    public BooleanValueContainerEntry(ITextComponent displayName, String name, boolean value)
    {
        this.displayName = displayName;
        this.name = name;
        this.previousValue = value;
        this.value = value;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public InputType getInputType()
    {
        return InputType.TOGGLE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getValue()
    {
        return (E) value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getPreviousValue()
    {
        return (E) previousValue;
    }

    @Override
    public boolean isDirty()
    {
        return this.value != this.previousValue;
    }

    @Override
    public String getDisplay()
    {
        return Boolean.toString(this.value);
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        nbt.putBoolean(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_BYTE) ? nbt.getBoolean(this.getName()) : this.previousValue;
    }

    @Override
    public void parse(Object data)
    {
        this.value = data instanceof Boolean ? (Boolean) data : Boolean.parseBoolean(String.valueOf(data));
    }

    @Override
    public boolean isValid(Object data)
    {
        return data instanceof Boolean || data instanceof String;
    }

    @Override
    public boolean isToggled()
    {
        return this.value;
    }
}
