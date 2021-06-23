package io.github.ocelot.sonar.client.render.forge;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ocelot.sonar.client.render.StructureTemplateRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class StructureTemplateRendererImpl
{
    public static List<StructureTemplate.StructureBlockInfo> getTemplateBlocks(@Nullable StructureTemplate template)
    {
        if (template == null)
            return Collections.emptyList();
        List<StructureTemplate.Palette> blockInfos = ObfuscationReflectionHelper.getPrivateValue(StructureTemplate.class, template, "field_204769_a");
        if (blockInfos == null)
            return Collections.emptyList();
        List<StructureTemplate.StructureBlockInfo> blocks = blockInfos.get(0).blocks();
        blocks.removeIf(block -> block.state.isAir());
        return blocks;
    }

    public static void renderBlocks(PoseStack matrixstack, BlockAndTintGetter level, Set<BlockPos> positions, StructureTemplateRenderer.CompiledChunk compiledChunkIn, ChunkBufferBuilderPack builderIn, Random random)
    {
        BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
        for (BlockPos blockpos2 : positions)
        {
            BlockState blockstate = level.getBlockState(blockpos2);

            FluidState fluidstate = level.getFluidState(blockpos2);
            for (RenderType rendertype : RenderType.chunkBufferLayers())
            {
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
                if (!fluidstate.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluidstate, rendertype))
                {
                    BufferBuilder bufferbuilder = builderIn.builder(rendertype);
                    if (compiledChunkIn.layersStarted.add(rendertype))
                    {
                        bufferbuilder.begin(7, DefaultVertexFormat.BLOCK);
                    }

                    matrixstack.pushPose();
                    matrixstack.translate((int) (blockpos2.getX() / 16.0) * 16.0, (int) (blockpos2.getY() / 16.0) * 16.0, (int) (blockpos2.getZ() / 16.0) * 16.0);
                    if (blockrendererdispatcher.renderLiquid(blockpos2, level, new StructureTemplateRenderer.LiquidVertexBuffer(bufferbuilder, matrixstack.last().pose(), matrixstack.last().normal()), fluidstate))
                    {
                        compiledChunkIn.layersUsed.add(rendertype);
                    }
                    matrixstack.popPose();
                }

                if (blockstate.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(blockstate, rendertype))
                {
                    BufferBuilder bufferbuilder2 = builderIn.builder(rendertype);
                    if (compiledChunkIn.layersStarted.add(rendertype))
                    {
                        bufferbuilder2.begin(7, DefaultVertexFormat.BLOCK);
                    }

                    matrixstack.pushPose();
                    matrixstack.translate(blockpos2.getX(), blockpos2.getY(), blockpos2.getZ());
                    if (blockrendererdispatcher.renderModel(blockstate, blockpos2, level, matrixstack, bufferbuilder2, true, random, EmptyModelData.INSTANCE))
                    {
                        compiledChunkIn.layersUsed.add(rendertype);
                    }

                    matrixstack.popPose();
                }
            }
        }
        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
    }

    public static void createBlockEntity(BlockGetter level, Map<BlockPos, BlockEntity> tileEntities, StructureTemplate.StructureBlockInfo info)
    {
        if (info.state.hasTileEntity())
        {
            tileEntities.put(info.pos, info.state.createTileEntity(level));
        }
    }
}
