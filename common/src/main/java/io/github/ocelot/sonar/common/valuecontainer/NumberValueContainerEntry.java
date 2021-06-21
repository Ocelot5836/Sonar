package io.github.ocelot.sonar.common.valuecontainer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>A {@link ValueContainerEntry} that supports standard {@link Number} type</p>
 *
 * @param <T> The type of number this entry is for
 * @author Ocelot
 * @since 2.1.0
 */
public abstract class NumberValueContainerEntry<T extends Number> implements ValueContainerEntry<T>, SliderEntry
{
    private final String name;
    private final T minValue;
    private final T maxValue;
    private final T previousValue;
    protected T value;
    private Predicate<String> validator;
    private boolean percentage;

    public NumberValueContainerEntry(String name, T value, T minValue, T maxValue)
    {
        this.name = name;
        this.previousValue = value;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.validator = createDefaultValidator(this);
    }

    /**
     * Converts the provided number into this number type.
     *
     * @param number The number to convert
     * @return The converted value
     */
    protected abstract T getValue(Number number);

    /**
     * Clamps the provided number between the min and max values.
     *
     * @param value    The value to clamp
     * @param minValue The minimum number the value can
     * @param maxValue The maximum number the value can be
     * @return The clamped value
     */
    protected abstract T clamp(T value, T minValue, T maxValue);

    /**
     * @return Whether or not this entry has both upper and lower bounds
     */
    protected abstract boolean hasBounds();

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * @return The minimum value this number can be
     */
    public T getMinValue()
    {
        return minValue;
    }

    /**
     * @return The maximum value this number can be
     */
    public T getMaxValue()
    {
        return maxValue;
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
    public InputType getInputType()
    {
        return this.hasBounds() ? InputType.SLIDER : InputType.TEXT_FIELD;
    }

    @Override
    public boolean isDirty()
    {
        return !Objects.equals(this.value, this.previousValue);
    }

    @Override
    public String getDisplay()
    {
        return String.valueOf(this.value);
    }

    @Override
    public void parse(String data)
    {
        this.value = this.clamp(this.getValue(NumberUtils.createNumber(data.trim())), this.minValue, this.maxValue);
    }

    @Override
    public double getSliderValue()
    {
        return ((Number) this.getValue()).doubleValue();
    }

    @Override
    public double getMinSliderValue()
    {
        return this.getMinValue().doubleValue();
    }

    @Override
    public double getMaxSliderValue()
    {
        return this.getMaxValue().doubleValue();
    }

    @Override
    public boolean isPercentage()
    {
        return percentage;
    }

    /**
     * Sets whether or not this entry should render as a percentage when using a slider.
     *
     * @param percentage Whether or not to use percentages
     */
    public NumberValueContainerEntry<T> setPercentage(boolean percentage)
    {
        this.percentage = percentage;
        return this;
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
    public NumberValueContainerEntry<T> setValidator(@Nullable Predicate<String> validator)
    {
        this.validator = validator;
        return this;
    }

    /**
     * Generates the default validator for the specified {@link NumberValueContainerEntry}.
     *
     * @param entry The entry to create the validator for
     * @return A new predicate that will be used for text area parsing
     */
    public static Predicate<String> createDefaultValidator(NumberValueContainerEntry<?> entry)
    {
        return s -> !StringUtils.isEmpty(s.trim()) && NumberUtils.isCreatable(s.trim());
    }
}
