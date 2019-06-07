package com.example.examplemod.init;

import com.ocelot.api.init.Registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class ExampleBlocks
{
	public static final Block EXAMPLE_BLOCK = new Block(Material.ROCK, MapColor.SNOW).setRegistryName("block").setUnlocalizedName("block");

	public static void init(Registry reg)
	{
		reg.registerBlock(EXAMPLE_BLOCK);
	}
}
