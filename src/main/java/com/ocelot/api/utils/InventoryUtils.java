/*******************************************************************************
 * Copyright 2019 Brandon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.ocelot.api.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * 
 * Has some useful methods for getting, removing, and dealing with item stacks.
 * 
 * @author Ocelot5836
 * @since Jun 6, 2019
 * 
 */
public class InventoryUtils
{
	/**
	 * @param player The player to check
	 * @param item   The item to be searched for
	 * @return The amount of items found
	 */
	public static int getItemAmount(EntityPlayer player, Item item)
	{
		int amount = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack tempStack = player.inventory.getStackInSlot(i);
			if (!tempStack.isEmpty() && tempStack.getItem() == item)
			{
				amount += tempStack.getCount();
			}
		}
		return amount;
	}

	/**
	 * @param player The player to check
	 * @param item   The item to be searched for
	 * @param meta   The metadata to be searched for
	 * @return The amount of items found
	 */
	public static int getItemAmount(EntityPlayer player, Item item, int meta)
	{
		int amount = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack tempStack = player.inventory.getStackInSlot(i);
			if (!tempStack.isEmpty() && tempStack.getItem() == item && tempStack.getMetadata() == meta)
			{
				amount += tempStack.getCount();
			}
		}
		return amount;
	}

	/**
	 * @param player The player to check
	 * @param stack  The item stack to be searched for. Can be used for meta
	 *               searching as well as item searching
	 * @return The amount of items found
	 */
	public static int getStackAmount(EntityPlayer player, ItemStack stack)
	{
		int amount = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack tempStack = player.inventory.getStackInSlot(i);
			if (!tempStack.isEmpty() && ItemStack.areItemStacksEqual(tempStack, stack))
			{
				amount += tempStack.getCount();
			}
		}
		return amount;
	}

	/**
	 * @param player The player to check
	 * @param item   The item being searched for
	 * @return The slot at which the item was found
	 */
	public static int getItemSlot(EntityPlayer player, Item item)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack tempStack = player.inventory.getStackInSlot(i);
			if (!tempStack.isEmpty() && tempStack.getItem() == item)
			{
				return i;
			}
		}
		return 0;
	}

	/**
	 * @param player The player to check
	 * @param item   The item being searched for
	 * @param meta   The metadata to search for
	 * @return The slot at which the item was found
	 */
	public static int getItemSlot(EntityPlayer player, Item item, int meta)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == item && stack.getMetadata() == meta)
			{
				return i;
			}
		}
		return 0;
	}

	/**
	 * @param player The player to check
	 * @param stack  The stack being searched for
	 * @return The slot at which the item was found
	 */
	public static int getItemSlot(EntityPlayer player, ItemStack stack)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack tempStack = player.inventory.getStackInSlot(i);
			if (!tempStack.isEmpty() && ItemStack.areItemStacksEqual(tempStack, stack))
			{
				return i;
			}
		}
		return 0;
	}

	/**
	 * @param player The player to check
	 * @param item   The item to search for
	 * @param amount The amount to search for
	 * @return If the amount of items is in the player's inventory
	 */
	public static boolean hasItemAndAmount(EntityPlayer player, Item item, int amount)
	{
		int count = 0;
		for (ItemStack tempStack : player.inventory.mainInventory)
		{
			if (!tempStack.isEmpty() && tempStack.getItem() == item)
			{
				count += tempStack.getCount();
			}
		}
		return amount <= count;
	}

	/**
	 * @param player The player to check
	 * @param item   The item to search for
	 * @param amount The amount to search for
	 * @param meta   The meta data of the item to search for
	 * @return If the amount of items is in the player's inventory
	 */
	public static boolean hasItemAndAmount(EntityPlayer player, Item item, int amount, int meta)
	{
		int count = 0;
		for (ItemStack tempStack : player.inventory.mainInventory)
		{
			if (!tempStack.isEmpty() && tempStack.getItem() == item && tempStack.getMetadata() == meta)
			{
				count += tempStack.getCount();
			}
		}
		return amount <= count;
	}

	/**
	 * @param player The player to check
	 * @param stack  The stack to search for
	 * @param amount The amount to search for
	 * @return If the amount of items is in the player's inventory
	 */
	public static boolean hasStackAndAmount(EntityPlayer player, ItemStack stack, int amount)
	{
		int count = 0;
		for (ItemStack tempStack : player.inventory.mainInventory)
		{
			if (!tempStack.isEmpty() && ItemStack.areItemStacksEqual(tempStack, stack))
			{
				count += tempStack.getCount();
			}
		}
		return amount <= count;
	}

	/**
	 * @param player The player to check
	 * @param item   The item to remove
	 * @param amount The amount to remove
	 * @return If the player has and removed the items specified
	 */
	public static boolean removeItemWithAmount(EntityPlayer player, Item item, int amount)
	{
		if (hasItemAndAmount(player, item, amount))
		{
			for (int i = 0; i < player.inventory.getSizeInventory(); i++)
			{
				ItemStack tempStack = player.inventory.getStackInSlot(i);
				if (!tempStack.isEmpty() && tempStack.getItem() == item)
				{
					if (amount - tempStack.getCount() < 0)
					{
						tempStack.shrink(amount);
						return true;
					}
					else
					{
						amount -= tempStack.getCount();
						player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						if (amount == 0)
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param player The player to check
	 * @param item   The item to remove
	 * @param amount The amount to remove
	 * @param meta   The meta data of the item to remove
	 * @return If the player has and removed the items specified
	 */
	public static boolean removeItemWithAmount(EntityPlayer player, Item item, int amount, int meta)
	{
		if (hasItemAndAmount(player, item, amount, meta))
		{
			for (int i = 0; i < player.inventory.getSizeInventory(); i++)
			{
				ItemStack tempStack = player.inventory.getStackInSlot(i);
				if (!tempStack.isEmpty() && tempStack.getItem() == item && tempStack.getMetadata() == meta)
				{
					if (amount - tempStack.getCount() < 0)
					{
						tempStack.shrink(amount);
						return true;
					}
					else
					{
						amount -= tempStack.getCount();
						player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						if (amount == 0)
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param player The player to check
	 * @param stack  The item to remove
	 * @param amount The amount to remove
	 * @return If the player has and removed the items specified
	 */
	public static boolean removeStackWithAmount(EntityPlayer player, ItemStack stack, int amount)
	{
		if (hasStackAndAmount(player, stack, amount))
		{
			for (int i = 0; i < player.inventory.getSizeInventory(); i++)
			{
				ItemStack tempStack = player.inventory.getStackInSlot(i);
				if (!tempStack.isEmpty() && ItemStack.areItemStacksEqual(tempStack, stack))
				{
					if (amount - tempStack.getCount() < 0)
					{
						tempStack.shrink(amount);
						return true;
					}
					else
					{
						amount -= tempStack.getCount();
						player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						if (amount == 0)
							return true;
					}
				}
			}
		}
		return false;
	}
}
