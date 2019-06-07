package com.example.examplemod.init;

import com.ocelot.api.init.Registry;

import net.minecraft.item.Item;

public class ExampleItems
{
	public static final Item EXAMPLE_ITEM = new Item().setRegistryName("item").setUnlocalizedName("item");

	public static void init(Registry reg)
	{
		reg.registerItem(EXAMPLE_ITEM);
	}
}
