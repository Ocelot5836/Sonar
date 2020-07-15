package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>A {@link NumberValueContainerEntry} that supports string data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public class StringValueContainerEntry implements ValueContainerEntry<String>
{
    private final ITextComponent displayName;
    private final String name;
    private final String previousValue;
    private String value;
    private Predicate<String> validator;

    public StringValueContainerEntry(ITextComponent displayName, String name, String value)
    {
        this.displayName = displayName;
        this.name = name;
        this.previousValue = value;
        this.value = value;
        this.validator = null;
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
        return InputType.TEXT_FIELD;
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
        return !this.value.equals(this.previousValue);
    }

    @Override
    public String getDisplay()
    {
        return value;
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        nbt.putString(this.getName(), this.value);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_STRING) ? nbt.getString(this.getName()) : this.previousValue;
    }

    @Override
    public void parse(String data)
    {
        this.value = data;
    }

    @Override
    public Optional<Predicate<String>> getValidator()
    {
        return Optional.ofNullable(this.validator);
    }

    /**
     * Sets the validator to the specified value.
     *
     * @param validator The new validator value or null for no validator
     */
    public StringValueContainerEntry setValidator(@Nullable Predicate<String> validator)
    {
        this.validator = validator;
        return this;
    }
}
