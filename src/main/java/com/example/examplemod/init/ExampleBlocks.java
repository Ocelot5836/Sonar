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
