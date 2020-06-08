package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * <p>Specifies this block has special parameters that can be modified by clients.</p>
 * <p>To set up a basic framework, reference {@link DefaultValueContainerServerFunctionality} && {@link DefaultValueContainerClientFunctionality}</p>
 *
 * @author Ocelot
 * @see ValueContainerEntry
 * @see DefaultValueContainerServerFunctionality
 * @see DefaultValueContainerClientFunctionality
 * @since 2.1.0
 */
public interface ValueContainer
{
    /**
     * Fills the specified list with the required entries.
     *
     * @param world   The world this container is in
     * @param pos     The pos this container is in
     * @param entries The list to add entries to
     */
    void getEntries(World world, BlockPos pos, List<ValueContainerEntry<?>> entries);

    /**
     * Reads the data from the provided entries.
     *
     * @param world   The world this container is in
     * @param pos     The pos this container is in
     * @param entries The entries to read from
     */
    void readEntries(World world, BlockPos pos, Map<String, ValueContainerEntry<?>> entries);

    /**
     * Fetches a list of entries from this container.
     *
     * @param world The world this container is in
     * @param pos   The pos this container is in
     * @return A list full of the entries from {@link #getEntries(World, BlockPos, List)}
     */
    default List<ValueContainerEntry<?>> getEntries(World world, BlockPos pos)
    {
        List<ValueContainerEntry<?>> entries = new ArrayList<>();
        this.getEntries(world, pos, entries);
        return entries;
    }

    /**
     * Fetches the title of this container.
     *
     * @param world The world this container is in
     * @param pos   The pos this container is in
     * @return The title of this container or null to use the default title
     */
    Optional<ITextComponent> getTitle(World world, BlockPos pos);

    /**
     * Serializes the container entry data.
     *
     * @param entries The entries to serialize
     * @return The tag full of data
     */
    static CompoundNBT serialize(List<ValueContainerEntry<?>> entries)
    {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT entriesNbt = new ListNBT();
        entries.forEach(valueContainerEntry ->
        {
            if (!valueContainerEntry.isDirty())
                return;
            try
            {
                CompoundNBT valueContainerEntryNbt = new CompoundNBT();

                valueContainerEntryNbt.putString("name", valueContainerEntry.getName());

                CompoundNBT entryDataNbt = new CompoundNBT();
                valueContainerEntry.write(entryDataNbt);
                valueContainerEntryNbt.put("data", entryDataNbt);

                entriesNbt.add(valueContainerEntryNbt);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        nbt.put("entries", entriesNbt);

        return nbt;
    }

    /**
     * Deserializes the specified container from NBT.
     *
     * @param container The container to deserialize
     * @param nbt       The tag full of data
     */
    static void deserialize(World world, BlockPos pos, ValueContainer container, CompoundNBT nbt)
    {
        Map<String, ValueContainerEntry<?>> entries = new HashMap<>();
        container.getEntries(world, pos).forEach(valueContainerEntry -> entries.put(valueContainerEntry.getName(), valueContainerEntry));

        Map<String, ValueContainerEntry<?>> deserializedEntries = new HashMap<>();

        ListNBT entriesNbt = nbt.getList("entries", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entriesNbt.size(); i++)
        {
            CompoundNBT valueContainerEntryNbt = entriesNbt.getCompound(i);
            String name = valueContainerEntryNbt.getString("name");
            try
            {
                if (!entries.containsKey(name))
                    throw new IllegalStateException("Expected to deserialize '" + name + "', but it is not a valid property!");

                ValueContainerEntry<?> valueContainerEntry = entries.get(name);
                valueContainerEntry.read(valueContainerEntryNbt.getCompound("data"));
                deserializedEntries.put(valueContainerEntry.getName(), valueContainerEntry);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (!deserializedEntries.isEmpty())
            container.readEntries(world, pos, deserializedEntries);
    }
}
