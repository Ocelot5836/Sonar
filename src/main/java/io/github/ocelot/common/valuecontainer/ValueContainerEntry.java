package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>Handles the serialization and deserialization of {@link ValueContainer} entries to/from the screen.</p>
 *
 * @param <T> The type of data stored in this entry
 * @author Ocelot
 * @since 2.1.0
 */
public interface ValueContainerEntry<T>
{
    /**
     * @return The internal name of this entry. Names must be unique from others to make sure they do not overlap
     */
    String getName();

    /**
     * @return The display name of this entry.
     */
    ITextComponent getDisplayName();

    /**
     * @return The type of input that will be used to handle user input
     */
    InputType getInputType();

    /**
     * @return The current value of this entry
     */
    <E> E getValue();

    /**
     * @return The value of this entry before it was modified
     */
    <E> E getPreviousValue();

    /**
     * @return Whether or not this value has changed
     */
    boolean isDirty();

    /**
     * @return A string representation of this data
     */
    String getDisplay();

    /**
     * @return The validator or null for no validator to be used
     */
    Optional<Predicate<String>> getValidator();

    /**
     * Writes this entry value to NBT.
     *
     * @param nbt The tag to put data into
     */
    void write(CompoundNBT nbt);

    /**
     * Reads this entry value from NBT.
     *
     * @param nbt The tag containing data
     */
    void read(CompoundNBT nbt);

    /**
     * Parses this type of data from the provided data.
     *
     * @param data The data to parse
     */
    void parse(String data);

    /**
     * <p>The type of input an entry can be.</p>
     *
     * @author Ocelot
     */
    enum InputType
    {
        TEXT_FIELD, TOGGLE, SWITCH, SLIDER
    }
}
