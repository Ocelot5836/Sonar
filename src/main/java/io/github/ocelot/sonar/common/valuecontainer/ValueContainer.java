package io.github.ocelot.sonar.common.valuecontainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

/**
 * <p>Specifies this block has special parameters that can be modified by clients.</p>
 *
 * @author Ocelot
 * @see ValueContainerEntry
 * @see IValueContainerClientHandler
 * @see IValueContainerServerHandler
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
     * Writes data into a new tag.
     *
     * @param world The world the value container is in
     * @param pos   The position of the value container
     * @return A tag of data to send or null to not send any
     */
    @Nullable
    default CompoundNBT writeClientValueContainer(World world, BlockPos pos)
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
    @OnlyIn(Dist.CLIENT)
    default void readClientValueContainer(World world, BlockPos pos, CompoundNBT nbt)
    {
    }

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
     * @return An optional containing the title for this container
     */
    @OnlyIn(Dist.CLIENT)
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

    /**
     * Retrieves a value container from the specified position.
     *
     * @param world The world to get the container from
     * @param pos   The position of the container
     * @return An optional containing the value container at that position
     */
    static Optional<ValueContainer> get(IBlockReader world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof ValueContainer)
            return Optional.ofNullable((ValueContainer) world.getTileEntity(pos));
        if (world.getBlockState(pos).getBlock() instanceof ValueContainer)
            return Optional.of((ValueContainer) world.getBlockState(pos).getBlock());
        return Optional.empty();
    }
}
