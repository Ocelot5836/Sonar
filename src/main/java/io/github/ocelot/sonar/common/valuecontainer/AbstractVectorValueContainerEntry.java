package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a vector type</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
public abstract class AbstractVectorValueContainerEntry<T> implements ValueContainerEntry<T>
{
    private final ITextComponent displayName;
    private final String name;
    private final T minValue;
    private final T maxValue;
    private final T previousValue;
    protected T value;
    private Predicate<String> validator;

    public AbstractVectorValueContainerEntry(ITextComponent displayName, String name, T value, @Nullable T minValue, @Nullable T maxValue, boolean allowDecimals)
    {
        this.displayName = displayName;
        this.name = name;
        this.previousValue = value;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.validator = createDefaultValidator(this, allowDecimals);
    }

    /**
     * Clamps this value between the two specified values.
     *
     * @param value    The value to be clamped
     * @param minValue The minimum value it can be
     * @param maxValue The maximum value it can be
     * @return The clamped value
     */
    protected T clamp(T value, @Nullable T minValue, @Nullable T maxValue)
    {
        if (minValue == null && maxValue == null)
            return value;

        double x = this.getX(value).doubleValue();
        double y = this.getY(value).doubleValue();
        double z = this.getZ(value).doubleValue();
        if (minValue != null)
        {
            if (x < this.getX(minValue).doubleValue())
                x = this.getX(minValue).doubleValue();
            if (y < this.getY(minValue).doubleValue())
                y = this.getY(minValue).doubleValue();
            if (z < this.getZ(minValue).doubleValue())
                z = this.getZ(minValue).doubleValue();
        }
        if (maxValue != null)
        {
            if (x > this.getX(maxValue).doubleValue())
                x = this.getX(maxValue).doubleValue();
            if (y < this.getX(maxValue).doubleValue())
                y = this.getX(maxValue).doubleValue();
            if (z < this.getX(maxValue).doubleValue())
                z = this.getX(maxValue).doubleValue();
        }
        return this.create(x, y, z);
    }

    /**
     * Creates a new Vector with the specified values.
     *
     * @param x The x value of the vector
     * @param y The y value of the vector
     * @param z The z value of the vector
     * @return A new Vector with the specified values
     */
    protected abstract T create(Number x, Number y, Number z);

    /**
     * Fetches the x value from the specified vector.
     *
     * @param value The vector to get the value from
     * @return The x value of the provided vector
     */
    protected abstract Number getX(T value);

    /**
     * Fetches the y value from the specified vector.
     *
     * @param value The vector to get the value from
     * @return The y value of the provided vector
     */
    protected abstract Number getY(T value);

    /**
     * Fetches the z value from the specified vector.
     *
     * @param value The vector to get the value from
     * @return The z value of the provided vector
     */
    protected abstract Number getZ(T value);

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

    /**
     * @return The minimum value this vector can be
     */
    @Nullable
    public T getMinValue()
    {
        return minValue;
    }

    /**
     * @return The maximum value this vector can be
     */
    @Nullable
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
        return InputType.TEXT_FIELD;
    }

    @Override
    public boolean isDirty()
    {
        return !Objects.equals(this.value, this.previousValue);
    }

    @Override
    public String getDisplay()
    {
        return this.getX(this.value) + "," + this.getY(this.value) + "," + this.getZ(this.value);
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
    public AbstractVectorValueContainerEntry<T> setValidator(@Nullable Predicate<String> validator)
    {
        this.validator = validator;
        return this;
    }

    /**
     * Generates the default validator for the specified entry.
     *
     * @param entry         The entry to create the validator for
     * @param allowDecimals Whether or not decimal values are allowed
     * @return A new predicate that will be used for text area parsing
     */
    public static Predicate<String> createDefaultValidator(ValueContainerEntry<?> entry, boolean allowDecimals)
    {
        return s ->
        {
            String[] tokens = s.split(",", 4);
            if (tokens.length != 3)
                return false;
            for (String item : tokens)
            {
                String token = item.trim();
                if (StringUtils.isEmpty(token) || !NumberUtils.isCreatable(token))
                    return false;
                if (!allowDecimals)
                {
                    Number value = NumberUtils.createNumber(token);
                    if (value instanceof Double || value instanceof Float)
                        return false;
                }
            }
            return true;
        };
    }
}
