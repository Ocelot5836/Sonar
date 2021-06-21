package io.github.ocelot.sonar.common.valuecontainer;

import io.github.ocelot.sonar.common.util.SonarNBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>A {@link SwitchEntry} for array types.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public class ArrayValueContainerEntry<T> implements ValueContainerEntry<T>, SwitchEntry
{
    private final Component displayName;
    private final String name;
    private final T[] values;
    private final int previousIndex;
    private int index;
    private Function<T, String> displayGenerator;
    private Predicate<String> validator;

    public ArrayValueContainerEntry(Component displayName, String name, T[] values)
    {
        this(displayName, name, values, 0);
    }

    public ArrayValueContainerEntry(Component displayName, String name, T[] values, T value)
    {
        this(displayName, name, values, getIndex(values, value));
    }

    public ArrayValueContainerEntry(Component displayName, String name, T[] values, int index)
    {
        this.displayName = displayName;
        this.name = name;
        this.values = values;
        this.previousIndex = index;
        this.index = index;
        this.displayGenerator = null;
        this.validator = createDefaultValidator();
    }

    @Override
    public Component getDisplayName()
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
        return InputType.SWITCH;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getValue()
    {
        return (E) this.values[this.index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getPreviousValue()
    {
        return (E) this.values[this.previousIndex];
    }

    @Override
    public boolean isDirty()
    {
        return this.index != this.previousIndex;
    }

    @Override
    public String getDisplay()
    {
        return this.displayGenerator == null ? String.valueOf(this.<Object>getValue()) : this.displayGenerator.apply(this.getValue());
    }

    @Override
    public void write(CompoundTag nbt)
    {
        nbt.putInt(this.getName(), this.index);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.index = nbt.contains(this.getName(), SonarNBTConstants.TAG_ANY_NUMERIC) ? nbt.getInt(this.getName()) : this.previousIndex;
    }

    @Override
    public void parse(String data)
    {
        int index = NumberUtils.createNumber(data).intValue();
    }

    /**
     * @return The function used to create display names
     */
    public Function<T, String> getDisplayGenerator()
    {
        return displayGenerator;
    }

    @Override
    public Optional<Predicate<String>> getValidator()
    {
        return Optional.ofNullable(this.validator);
    }

    /**
     * Sets the function that will be used to display values.
     *
     * @param displayGenerator The function to create display names
     */
    public ArrayValueContainerEntry<T> setDisplayGenerator(@Nullable Function<T, String> displayGenerator)
    {
        this.displayGenerator = displayGenerator;
        return this;
    }

    /**
     * Sets the validator to the specified value.
     *
     * @param validator The new validator value or null for no validator
     */
    public ArrayValueContainerEntry<T> setValidator(@Nullable Predicate<String> validator)
    {
        this.validator = validator;
        return this;
    }

    /**
     * Generates the default validator for array entries.
     *
     * @return A new predicate that will be used for text area parsing
     */
    public static Predicate<String> createDefaultValidator()
    {
        return s -> !StringUtils.isEmpty(s.trim()) && NumberUtils.isCreatable(s.trim());
    }

    @Override
    public void showNext()
    {
        this.index++;
        if (this.index >= this.values.length)
            this.index = 0;
    }

    @Override
    public void showPrevious()
    {
        this.index--;
        if (this.index < 0)
            this.index = this.values.length - 1;
    }

    private static int getIndex(Object[] values, Object value)
    {
        int index = ArrayUtils.indexOf(values, value);
        return index == -1 ? 0 : index;
    }
}
