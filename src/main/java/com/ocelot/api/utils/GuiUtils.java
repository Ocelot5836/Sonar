package com.ocelot.api.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * <em><b>Copyright (c) 2019 Ocelot5836.</b></em><p>
 * 
 * This class contains some utility methods that would be most useful when
 * rendering a GUI.
 * 
 * @author Ocelot5836
 * @since Jun 6, 2019
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiUtils
{
	/** The gui instance used to make GUI render calls */
	private static final Gui GUI = new Gui();
	/** The logger instance for logging */
	private static final Logger LOGGER = LogManager.getLogger();

	private GuiUtils()
	{
	}

	/**
	 * Plays the default minecraft button click sound as full pitch.
	 */
	public static void playButtonClick()
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	/**
	 * Checks if the mouse is inside of the specified location.
	 * 
	 * @param x      The x position to check
	 * @param y      The y position to check
	 * @param width  The width of the check area
	 * @param height The height of the check area
	 * @param mouseX The x of the mouse
	 * @param mouseY The y of the mouse
	 * @return Whether the mouse is inside of that location
	 */
	public static boolean isMouseInside(int x, int y, int width, int height, int mouseX, int mouseY)
	{
		return mouseX >= x && mouseX < (x + width) && mouseY >= y && mouseY < (y + height);
	}

	/**
	 * Draws an expanding gui with the specified parameters.
	 * 
	 * @param x      The x to draw the window at
	 * @param y      The y to draw the window at
	 * @param width  The width of the window
	 * @param height The height of the window
	 * @param type   The type of gui to draw
	 */
	public static void drawCustomSizeGui(int x, int y, int width, int height, GuiType type)
	{
		TextureUtils.bindTexture("ocelotutil", "textures/gui/util.png");
		GlStateManager.color(1F, 1F, 1F, 1F);

		int u = type.getU();
		int v = type.getV();
		int cellSize = type.getCellSize();

		GuiScreen.drawScaledCustomSizeModalRect(x + cellSize, y + cellSize, u + cellSize, v + cellSize, cellSize, cellSize, width - cellSize * 2, height - cellSize * 2, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x, y + cellSize, u, v + cellSize, cellSize, cellSize, cellSize, height - cellSize * 2, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x + width - cellSize, y + cellSize, u + cellSize * 2, v + cellSize, cellSize, cellSize, cellSize, height - cellSize * 2, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x + cellSize, y + height - cellSize, u + cellSize, v + cellSize * 2, cellSize, cellSize, width - cellSize * 2, cellSize, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x + cellSize, y, u + cellSize, v, cellSize, cellSize, width - cellSize * 2, cellSize, 256, 256);

		GUI.drawTexturedModalRect(x, y, u, v, cellSize, cellSize);
		GUI.drawTexturedModalRect(x + width - cellSize, y, u + cellSize * 2, v, cellSize, cellSize);
		GUI.drawTexturedModalRect(x, y + height - cellSize, u, v + cellSize * 2, cellSize, cellSize);
		GUI.drawTexturedModalRect(x + width - cellSize, y + height - cellSize, u + cellSize * 2, v + cellSize * 2, cellSize, cellSize);
	}

	/**
	 * Draws a default minecraft slot at the specified position with the specified
	 * size.
	 * 
	 * @param x      The x position
	 * @param y      The y position
	 * @param width  The width of the slot
	 * @param height The height of the slot
	 */
	public static void drawSlot(int x, int y, int width, int height)
	{
		TextureUtils.bindTexture("ocelotutil", "textures/gui/util.png");
		GlStateManager.color(1F, 1F, 1F, 1F);
		GuiScreen.drawScaledCustomSizeModalRect(x + 1, y + 1, 1, 16, 0, 0, width - 2, height - 2, 256, 256);

		GuiScreen.drawScaledCustomSizeModalRect(x, y, 0, 15, 1, 1, 1, height - 1, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x + 1, y, 0, 15, 1, 1, width - 2, 1, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x + (width - 1), y, 2, 16, 1, 1, 1, height - 1, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x + 1, y + (height - 1), 1, 17, 1, 1, width - 1, 1, 256, 256);

		GuiScreen.drawScaledCustomSizeModalRect(x + (width - 1), y, 2, 15, 1, 1, 1, 1, 256, 256);
		GuiScreen.drawScaledCustomSizeModalRect(x, y + (height - 1), 0, 17, 1, 1, 1, 1, 256, 256);
	}

	/**
	 * Renders an entity to the screen.
	 * 
	 * @param x      The x position
	 * @param y      The y position
	 * @param scale  The scale of the entity
	 * @param mouseX The mouse x position
	 * @param mouseY The mouse y position
	 * @param entity The entity to render
	 */
	public static void drawEntityOnScreen(int x, int y, int scale, float mouseX, float mouseY, EntityLivingBase entity)
	{
		if (entity != null)
		{
			GlStateManager.enableColorMaterial();
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, (float) y, 50.0F);
			GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			float f = entity.renderYawOffset;
			float f1 = entity.rotationYaw;
			float f2 = entity.rotationPitch;
			float f3 = entity.prevRotationYawHead;
			float f4 = entity.rotationYawHead;
			GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
			RenderHelper.enableStandardItemLighting();
			GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
			entity.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
			entity.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
			entity.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
			entity.rotationYawHead = entity.rotationYaw;
			entity.prevRotationYawHead = entity.rotationYaw;
			GlStateManager.translate(0.0F, 0.0F, 0.0F);
			RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
			rendermanager.setPlayerViewY(180.0F);
			rendermanager.setRenderShadow(false);
			rendermanager.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
			rendermanager.setRenderShadow(true);
			entity.renderYawOffset = f;
			entity.rotationYaw = f1;
			entity.rotationPitch = f2;
			entity.prevRotationYawHead = f3;
			entity.rotationYawHead = f4;
			GlStateManager.popMatrix();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.disableTexture2D();
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		else
		{
			LOGGER.error("Trying to render an entity that is null!");
		}
	}

	/**
	 * Specifies the type of gui to draw.
	 */
	public static class GuiType
	{
		public static final GuiType DEFAULT = new GuiType(0, 0, 3, 3, 5);
		public static final GuiType BOOK = new GuiType(15, 0, 3, 3, 8);

		private int u, v, width, height, cellSize;

		private GuiType(int u, int v, int width, int height, int cellSize)
		{
			this.u = u;
			this.v = v;
			this.width = width;
			this.height = height;
			this.cellSize = cellSize;
		}

		/**
		 * @return The x coordinate of the texture
		 */
		public int getU()
		{
			return u;
		}

		/**
		 * @return The y coordinate of the texture
		 */
		public int getV()
		{
			return v;
		}

		/**
		 * @return The width of the texture
		 */
		public int getWidth()
		{
			return width;
		}

		/**
		 * @return The height of the texture
		 */
		public int getHeight()
		{
			return height;
		}

		/**
		 * @return The size of each cell in the gui
		 */
		public int getCellSize()
		{
			return cellSize;
		}
	}
}
