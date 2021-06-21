package io.github.ocelot.sonar.common.valuecontainer;

import io.github.ocelot.sonar.common.util.SonarNBTConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <p>Specifies this block has special parameters that can be modified by clients.</p>
 *
 * @author Ocelot
 * @see ValueContainerEntry
 * @see ValueContainerClientHandler
 * @see ValueContainerServerHandler
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
    void getEntries(Level world, BlockPos pos, List<ValueContainerEntry<?>> entries);

    /**
     * Reads the data from the provided entries.
     *
     * @param world   The world this container is in
     * @param pos     The pos this container is in
     * @param entries The entries to read from
     */
    void readEntries(Level world, BlockPos pos, Map<String, ValueContainerEntry<?>> entries);

    /**
     * Writes data into a new tag.
     *
     * @param world The world the value container is in
     * @param pos   The position of the value container
     * @return A tag of data to send or null to not send any
     */
    @Nullable
    default CompoundTag writeClientValueContainer(Level world, BlockPos pos)
    {
        return null;
    }

    /**
     * Reads data from the specified tag.
     *
     * @param world The world the value container is in
     * @param pos   The position of the value container
     * @param nbt   The server data tag
     */
    @Environment(EnvType.CLIENT)
    default void readClientValueContainer(Level world, BlockPos pos, CompoundTag nbt)
    {
    }

    /**
     * Fetches a list of entries from this container.
     *
     * @param world The world this container is in
     * @param pos   The pos this container is in
     * @return A list full of the entries from {@link #getEntries(Level, BlockPos, List)}
     */
    default List<ValueContainerEntry<?>> getEntries(Level world, BlockPos pos)
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
     * @return An optional containing the title for this container
     */
    @Environment(EnvType.CLIENT)
    Optional<Component> getTitle(Level world, BlockPos pos);

    /**
     * Serializes the container entry data.
     *
     * @param entries The entries to serialize
     * @return The tag full of data
     */
    static CompoundTag serialize(List<ValueContainerEntry<?>> entries)
    {
        CompoundTag nbt = new CompoundTag();

        ListTag entriesNbt = new ListTag();
        entries.forEach(valueContainerEntry ->
        {
            if (!valueContainerEntry.isDirty())
                return;
            try
            {
                CompoundTag valueContainerEntryNbt = new CompoundTag();

                valueContainerEntryNbt.putString("name", valueContainerEntry.getName());

                CompoundTag entryDataNbt = new CompoundTag();
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
    static void deserialize(Level world, BlockPos pos, ValueContainer container, CompoundTag nbt)
    {
        Map<String, ValueContainerEntry<?>> entries = new HashMap<>();
        container.getEntries(world, pos).forEach(valueContainerEntry -> entries.put(valueContainerEntry.getName(), valueContainerEntry));

        Map<String, ValueContainerEntry<?>> deserializedEntries = new HashMap<>();

        ListTag entriesNbt = nbt.getList("entries", SonarNBTConstants.TAG_COMPOUND);
        for (int i = 0; i < entriesNbt.size(); i++)
        {
            CompoundTag valueContainerEntryNbt = entriesNbt.getCompound(i);
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

    /**
     * Retrieves a value container from the specified position.
     *
     * @param world The world to get the container from
     * @param pos   The position of the container
     * @return An optional containing the value container at that position
     */
    static Optional<ValueContainer> get(BlockGetter world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof ValueContainer)
            return Optional.ofNullable((ValueContainer) world.getBlockEntity(pos));
        if (world.getBlockState(pos).getBlock() instanceof ValueContainer)
            return Optional.of((ValueContainer) world.getBlockState(pos).getBlock());
        return Optional.empty();
    }
}
