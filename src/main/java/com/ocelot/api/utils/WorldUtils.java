package com.ocelot.api.utils;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * <em><b>Copyright (c) 2019 Ocelot5836.</b></em>
 * <p>
 * 
 * Contains some methods that help when manipulating the world.
 * 
 * @author Ocelot5836
 * @since Jun 6, 2019
 * 
 */
public class WorldUtils
{
	/**
	 * Ray traces from the player's head to the distance supplied.
	 * 
	 * @param distance The distance of the ray search area
	 * @return The result of that ray trace
	 */
	@Nullable
	public static RayTraceResult rayTrace(double distance)
	{
		Minecraft mc = Minecraft.getMinecraft();
		float partialTicks = mc.getRenderPartialTicks();

		Entity pointedEntity;
		double d0 = distance;
		RayTraceResult omo = mc.getRenderViewEntity().rayTrace(d0, partialTicks);
		double d1 = d0;
		Vec3d vec3 = mc.getRenderViewEntity().getPositionEyes(partialTicks);
		Vec3d vec31 = mc.getRenderViewEntity().getLook(partialTicks);
		Vec3d vec32 = vec3.addVector(vec31.x * d0, vec31.y * d0, vec31.z * d0);
		pointedEntity = null;
		Vec3d vec33 = null;
		float f1 = 1.0F;
		List list = mc.world.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().grow(vec31.x * d0, vec31.y * d0, vec31.z * d0).expand((double) f1, (double) f1, (double) f1));
		double d2 = d1;

		for (int i = 0; i < list.size(); ++i)
		{
			Entity entity = (Entity) list.get(i);

			if (entity.canBeCollidedWith())
			{
				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double) f2, (double) f2, (double) f2);
				RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

				if (axisalignedbb.contains(vec3))
				{
					if (0.0D < d2 || d2 == 0.0D)
					{
						pointedEntity = entity;
						vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
						d2 = 0.0D;
					}
				}
				else if (movingobjectposition != null)
				{
					double d3 = vec3.distanceTo(movingobjectposition.hitVec);

					if (d3 < d2 || d2 == 0.0D)
					{
						if (entity == mc.getRenderViewEntity().getRidingEntity() && !entity.canRiderInteract())
						{
							if (d2 == 0.0D)
							{
								pointedEntity = entity;
								vec33 = movingobjectposition.hitVec;
							}
						}
						else
						{
							pointedEntity = entity;
							vec33 = movingobjectposition.hitVec;
							d2 = d3;
						}
					}
				}
			}
		}
		if (pointedEntity != null && (d2 < d1 || omo == null))
		{
			omo = new RayTraceResult(pointedEntity, vec33);

			if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
			{
				mc.pointedEntity = pointedEntity;
			}
		}
		return omo;
	}

	/**
	 * Renders a block at the specified position
	 * 
	 * @param world       The world
	 * @param state       The state to render
	 * @param pos         The position to render at
	 * @param tessellator The tessellator instance
	 * @param buffer      The buffer instance
	 */
	@SideOnly(Side.CLIENT)
	public static void renderBlock(World world, IBlockState state, BlockPos pos, Tessellator tessellator, BufferBuilder buffer)
	{
		buffer.begin(7, DefaultVertexFormats.BLOCK);
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(state), state, pos, buffer, false, MathHelper.getPositionRandom(pos));
		tessellator.draw();
	}
}
