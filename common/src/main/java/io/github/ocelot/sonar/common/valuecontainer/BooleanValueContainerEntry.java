package io.github.ocelot.sonar.common.valuecontainer;

import io.github.ocelot.sonar.common.util.SonarNBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>A {@link ValueContainerEntry} that supports boolean data types.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class BooleanValueContainerEntry implements ValueContainerEntry<Boolean>, ToggleEntry
{
    private final Component displayName;
    private final String name;
    private final Boolean previousValue;
    private Boolean value;
    private Predicate<String> validator;
    private boolean toggle;

    public BooleanValueContainerEntry(Component displayName, String name, boolean value)
    {
        this.displayName = displayName;
        this.name = name;
        this.previousValue = value;
        this.value = value;
        this.validator = createDefaultValidator();
        this.toggle = true;
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
        return this.toggle ? InputType.TOGGLE : InputType.TEXT_FIELD;
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
        return this.value.booleanValue() != this.previousValue.booleanValue();
    }

    @Override
    public String getDisplay()
    {
        return Boolean.toString(this.value);
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
    public BooleanValueContainerEntry setValidator(@Nullable Predicate<String> validator)
    {
        this.validator = validator;
        return this;
    }

    @Override
    public void write(CompoundTag nbt)
    {
        nbt.putBoolean(this.getName(), this.value);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.value = nbt.contains(this.getName(), SonarNBTConstants.TAG_BYTE) ? nbt.getBoolean(this.getName()) : this.previousValue;
    }

    @Override
    public void parse(String data)
    {
        this.value = Boolean.parseBoolean(data);
    }

    @Override
    public boolean isToggled()
    {
        return this.value;
    }

    /**
     * Sets whether or not this entry should appear as a text area or a toggle button.
     *
     * @param toggle The new button value
     */
    public BooleanValueContainerEntry setToggle(boolean toggle)
    {
        this.toggle = toggle;
        return this;
    }

    /**
     * Generates the default validator for boolean entries.
     *
     * @return A new predicate that will be used for text area parsing
     */
    public static Predicate<String> createDefaultValidator()
    {
        return s -> "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    }
}
