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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * Contains some methods to bind and manipulate textures easier.
 * 
 * @author Ocelot5836
 * @since Jun 6, 2019
 *
 */
@SideOnly(Side.CLIENT)
public class TextureUtils
{
	/** The {@link Minecraft} instance */
	private static final Minecraft MC = Minecraft.getMinecraft();
	/** The cache of resource locations created */
	private static final Map<String, ResourceLocation> TEXTURES = new HashMap<String, ResourceLocation>();

	/**
	 * Deletes the specified texture from memory.
	 * 
	 * @param texture The texture to delete
	 */
	public static void deleteTexture(ResourceLocation texture)
	{
		MC.getTextureManager().deleteTexture(texture);
	}

	/**
	 * Binds the specified texture.
	 * 
	 * @param texture The texture to bind
	 */
	public static void bindTexture(ResourceLocation texture)
	{
		MC.getTextureManager().bindTexture(texture);
	}

	/**
	 * Binds a texture using the specified domain and path.
	 * 
	 * @param domain The domain of the texture
	 * @param path   The path to the texture
	 */
	public static void bindTexture(String domain, String path)
	{
		String locationString = domain + ":" + path;
		TEXTURES.computeIfAbsent(locationString, key -> new ResourceLocation(key));
		MC.getTextureManager().bindTexture(TEXTURES.get(locationString));
	}

	/**
	 * Binds a texture using specified location.
	 * 
	 * @param path The location to the texture
	 */
	public static void bindTexture(String path)
	{
		String[] parts = ResourceLocation.splitObjectName(path);
		bindTexture(parts[0], parts[1]);
	}
}
