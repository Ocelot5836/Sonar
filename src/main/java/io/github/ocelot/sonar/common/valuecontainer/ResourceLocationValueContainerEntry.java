package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>A {@link NumberValueContainerEntry} that supports Minecraft {@link ResourceLocation}.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 */
public class ResourceLocationValueContainerEntry implements ValueContainerEntry<ResourceLocation>
{
    private final Component displayName;
    private final String name;
    private final ResourceLocation previousValue;
    private ResourceLocation value;
    private Predicate<String> validator;

    public ResourceLocationValueContainerEntry(Component displayName, String name, ResourceLocation value)
    {
        this.displayName = displayName;
        this.name = name;
        this.previousValue = value;
        this.value = value;
        this.validator = createDefaultValidator();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Component getDisplayName()
    {
        return displayName;
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
        return this.value.toString();
    }

    @Override
    public void write(CompoundTag nbt)
    {
        nbt.putString(this.getName(), this.value.toString());
    }

    @Override
    public void read(CompoundTag nbt)
    {
        this.value = nbt.contains(this.getName(), Constants.NBT.TAG_STRING) ? new ResourceLocation(nbt.getString(this.getName())) : this.previousValue;
    }

    @Override
    public void parse(String data)
    {
        this.value = new ResourceLocation(data);
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
    public ResourceLocationValueContainerEntry setValidator(@Nullable Predicate<String> validator)
    {
        this.validator = validator;
        return this;
    }

    /**
     * Generates the default validator for boolean entries.
     *
     * @return A new predicate that will be used for text area parsing
     */
    public static Predicate<String> createDefaultValidator()
    {
        return value -> ResourceLocation.tryParse(value) != null;
    }
}
