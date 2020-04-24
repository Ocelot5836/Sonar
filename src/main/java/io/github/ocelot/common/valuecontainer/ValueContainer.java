package io.github.ocelot.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * <p>Specifies this block has special parameters that can be modified by clients.</p>
 *
 * @author Ocelot
 * @see ValueContainerEntry
 * @since 2.1.0
 */
public interface ValueContainer
{
    /**
     * Fills the specified list with the required entries.
     *
     * @param entries The list to add entries to
     */
    void getEntries(List<ValueContainerEntry<?>> entries);

    /**
     * Reads the data from the provided entries.
     *
     * @param entries The entries to read from
     */
    void readEntries(Map<String, ValueContainerEntry<?>> entries);

    /**
     * @return A list full of the entries from {@link #getEntries(List)}
     */
    default List<ValueContainerEntry<?>> getEntries()
    {
        List<ValueContainerEntry<?>> entries = new ArrayList<>();
        this.getEntries(entries);
        return entries;
    }

    /**
     * @return The title of this container or null to use the default title
     */
    @OnlyIn(Dist.CLIENT)
    Optional<ITextComponent> getTitle();

    /**
     * @return The position of the tile entity
     * @deprecated Tile entities should not be the only types of value containers
     */
    default BlockPos getContainerPos()
    {
        return BlockPos.ZERO;
    }

    /**
     * Serializes the container entry data.
     *
     * @param entries The entries to serialize
     * @return The tag full of data
     * @deprecated Use {@link #serialize(List)} instead
     */
    static CompoundNBT serialize(ValueContainer container, List<ValueContainerEntry<?>> entries)
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
    static void deserialize(ValueContainer container, CompoundNBT nbt)
    {
        Map<String, ValueContainerEntry<?>> entries = new HashMap<>();
        container.getEntries().forEach(valueContainerEntry -> entries.put(valueContainerEntry.getName(), valueContainerEntry));

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
            container.readEntries(deserializedEntries);
    }
}
