package io.github.ocelot.sonar.common.util;

import net.minecraft.core.NonNullList;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>Automatically indexes and sorts an item group by.</p>
 *
 * @since 5.1.0
 */
public abstract class SortedItemGroup extends CreativeModeTab
{
    private final List<Supplier<? extends Item>> orderedItems;
    private final LazyLoadedValue<Map<Item, Integer>> indexedItems;

    public SortedItemGroup(String label)
    {
        super(label);
        this.orderedItems = new ArrayList<>();
        this.indexedItems = new LazyLoadedValue<>(this::indexItems);
    }

    public SortedItemGroup(int index, String label)
    {
        super(index, label);
        this.orderedItems = new ArrayList<>();
        this.indexedItems = new LazyLoadedValue<>(this::indexItems);
    }

    private Map<Item, Integer> indexItems()
    {
        Map<Item, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < this.orderedItems.size(); i++)
            indexMap.put(this.orderedItems.get(i).get(), i);
        return indexMap;
    }

    private int getIndex(Item item)
    {
        Map<Item, Integer> indexes = this.indexedItems.get();
        return indexes.containsKey(item) ? indexes.get(item) : indexes.size();
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> items)
    {
        super.fillItemList(items);
        items.sort((stack1, stack2) ->
        {
            int index1 = this.getIndex(stack1.getItem());
            int index2 = this.getIndex(stack2.getItem());
            if (this.indexedItems.get().containsKey(stack1.getItem()) || this.indexedItems.get().containsKey(stack2.getItem()))
                return Integer.compare(index1, index2); // Index by specified position

            if (stack1.getItem().getRegistryName() == null || stack2.getItem().getRegistryName() == null)
                return 1; // Cannot index, put at end

            return 1 + stack1.getItem().getRegistryName().compareTo(stack2.getItem().getRegistryName()); // Index by registry name at end
        });
    }

    /**
     * @return The order items should be sorted in
     */
    public List<Supplier<? extends Item>> getOrderedItems()
    {
        return orderedItems;
    }
}
