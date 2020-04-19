package io.github.ocelot.common.valuecontainer;

import net.minecraft.dispenser.IPosition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>Specifies a {@link ValueContainerEntry} as being for a {@link Vec3d} type</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class VectorValueContainerEntry implements ValueContainerEntry<Vec3d>, TextFieldEntry
{
    private ITextComponent displayName;
    private final String name;
    private final Vec3d minValue;
    private final Vec3d maxValue;
    private final Vec3d previousValue;
    private Vec3d value;
    private Predicate<String> validator;

    public VectorValueContainerEntry(ITextComponent displayName, String name, Vec3d value)
    {
        this(displayName, name, value, null, null);
    }

    public VectorValueContainerEntry(ITextComponent displayName, String name, Vec3d value, Vec3d minValue, Vec3d maxValue)
    {
        this.displayName = displayName;
        this.name = name;
        this.previousValue = value;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.validator = createDefaultValidator(this);
    }

    private Vec3d clamp(Vec3d value, @Nullable Vec3d minValue, @Nullable Vec3d maxValue)
    {
        if (minValue == null && maxValue == null)
            return value;

        double x = value.getX();
        double y = value.getY();
        double z = value.getZ();
        if (minValue != null)
        {
            if (x < minValue.getX())
                x = minValue.getX();
            if (y < minValue.getY())
                y = minValue.getY();
            if (z < minValue.getZ())
                z = minValue.getZ();
        }
        if (maxValue != null)
        {
            if (x > maxValue.getX())
                x = maxValue.getX();
            if (y < maxValue.getY())
                y = maxValue.getY();
            if (z < maxValue.getZ())
                z = maxValue.getZ();
        }
        return new Vec3d(x, y, z);
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

    /**
     * @return The minimum value this vector can be
     */
    @Nullable
    public Vec3d getMinValue()
    {
        return minValue;
    }

    /**
     * @return The maximum value this vector can be
     */
    @Nullable
    public Vec3d getMaxValue()
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
        return this.value.getX() + "," + this.value.getY() + "," + this.value.getZ();
    }

    @Override
    public void write(CompoundNBT nbt)
    {
        CompoundNBT valueNbt = new CompoundNBT();
        valueNbt.putDouble("x", this.value.getX());
        valueNbt.putDouble("y", this.value.getY());
        valueNbt.putDouble("z", this.value.getZ());
        nbt.put(this.getName(), valueNbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        if (nbt.contains(this.getName(), Constants.NBT.TAG_COMPOUND))
        {
            CompoundNBT valueNbt = nbt.getCompound(this.getName());
            this.value = new Vec3d(valueNbt.getDouble("x"), valueNbt.getDouble("y"), valueNbt.getDouble("z"));
        }
        else
        {
            this.value = new Vec3d(0, 0, 0);
        }
    }

    @Override
    public void parse(Object data)
    {
        if (data instanceof Vec3d)
        {
            this.value = this.clamp((Vec3d) data, this.minValue, this.maxValue);
            return;
        }
        if (data instanceof IPosition)
        {
            IPosition position = (IPosition) data;
            this.value = this.clamp(new Vec3d(position.getX(), position.getY(), position.getZ()), this.minValue, this.maxValue);
            return;
        }
        String[] tokens = String.valueOf(data).split(",");
        if (tokens.length != 3)
            return;
        this.value = this.clamp(new Vec3d(NumberUtils.createNumber(tokens[0].trim()).doubleValue(), NumberUtils.createNumber(tokens[1].trim()).doubleValue(), NumberUtils.createNumber(tokens[2].trim()).doubleValue()), this.minValue, this.maxValue);
    }

    @Override
    public boolean isValid(Object data)
    {
        return data instanceof IPosition || data instanceof String;
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
    public void setValidator(@Nullable Predicate<String> validator)
    {
        this.validator = validator;
    }

    /**
     * Generates the default validator for the specified {@link VectorValueContainerEntry}.
     *
     * @param entry The entry to create the validator for
     * @return A new predicate that will be used for text area parsing
     */
    public static Predicate<String> createDefaultValidator(VectorValueContainerEntry entry)
    {
        return s ->
        {
            if (!entry.isValid(s))
                return false;
            String[] tokens = s.split(",");
            if (tokens.length > 3)
                return false;
            for (String token : tokens)
            {
                if (!NumberUtils.isCreatable(token.trim()))
                    return false;
            }
            try
            {
                if (tokens.length == 3)
                    entry.parse(s);
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        };
    }
}
