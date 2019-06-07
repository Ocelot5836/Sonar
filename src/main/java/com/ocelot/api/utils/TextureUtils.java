package com.ocelot.api.utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * <em><b>Copyright (c) 2019 Ocelot5836.</b></em><br>
 * </br>
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
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final Map<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();

	/**
	 * Creates a texture from a buffered image.
	 * 
	 * @param image The image to become a texture
	 * @return The {@link ResourceLocation} to that texture
	 * @deprecated Texture management with buffered images should be handled by the
	 *             user
	 */
	public static ResourceLocation createBufferedImageTexture(BufferedImage image)
	{
		return mc.getTextureManager().getDynamicTextureLocation(" ", new DynamicTexture(image));
	}

	/**
	 * Creates a texture from a buffered image.
	 * 
	 * @param name  The name of this texture
	 * @param image The image to become a texture
	 * @return The {@link ResourceLocation} to that texture using the specified name
	 * @deprecated Texture management with buffered images should be handled by the
	 *             user
	 */
	public static ResourceLocation createBufferedImageTexture(String name, BufferedImage image)
	{
		return mc.getTextureManager().getDynamicTextureLocation(name, new DynamicTexture(image));
	}

	/**
	 * Deletes the specified texture from memory.
	 * 
	 * @param texture The texture to delete
	 */
	public static void deleteTexture(ResourceLocation texture)
	{
		mc.getTextureManager().deleteTexture(texture);
	}

	/**
	 * Binds the specified texture.
	 * 
	 * @param texture The texture to bind
	 */
	public static void bindTexture(ResourceLocation texture)
	{
		mc.getTextureManager().bindTexture(texture);
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
		textures.computeIfAbsent(locationString, key -> new ResourceLocation(key));
		mc.getTextureManager().bindTexture(textures.get(locationString));
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
