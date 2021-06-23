package io.github.ocelot.sonar.client.render.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ocelot.sonar.client.render.StructureTemplateRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

@ApiStatus.Internal
public class StructureTemplateRendererImpl
{
    private static final Field BLOCK_INFOS_FIELD;

    static
    {
        try
        {
            BLOCK_INFOS_FIELD = StructureTemplate.class.getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", StructureTemplate.class.getName(), "field_15586", "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;palettes:Ljava/util/List;"));
            BLOCK_INFOS_FIELD.setAccessible(true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<StructureTemplate.StructureBlockInfo> getTemplateBlocks(@Nullable StructureTemplate template)
    {
        if (template == null)
            return Collections.emptyList();
        try
        {
            List<StructureTemplate.Palette> blockInfos = (List<StructureTemplate.Palette>) BLOCK_INFOS_FIELD.get(template);//ObfuscationReflectionHelper.getPrivateValue(StructureTemplate.class, template, "field_204769_a");
            List<StructureTemplate.StructureBlockInfo> blocks = blockInfos.get(0).blocks();
            blocks.removeIf(block -> block.state.isAir());
            return blocks;
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    private static void renderBlocks(PoseStack matrixstack, BlockAndTintGetter level, Set<BlockPos> positions, StructureTemplateRenderer.CompiledChunk compiledChunkIn, ChunkBufferBuilderPack builderIn, Random random)
    {
        throw new UnsupportedOperationException("TODO fix");
//        BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
//        for (BlockPos blockpos2 : positions)
//        {
//            BlockState blockState = level.getBlockState(blockpos2);
//
//            FluidState fluidState = level.getFluidState(blockpos2);
//            RenderType renderType2;
//            BufferBuilder bufferBuilder2;
//            if (!fluidState.isEmpty()) {
//                renderType2 = ItemBlockRenderTypes.getRenderLayer(fluidState);
//                bufferBuilder2 = builderIn.builder(renderType2);
//                if (compiledChunkIn.hasLayer.add(renderType2)) {
//                    bufferBuilder2.begin(7, DefaultVertexFormat.BLOCK);
//                }
//
//                if (blockRenderDispatcher.renderLiquid(blockPos3, renderChunkRegion, bufferBuilder2, fluidState)) {
//                    compiledChunkIn.isCompletelyEmpty = false;
//                    compiledChunkIn.hasBlocks.add(renderType2);
//                }
//            }
//
//            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
//                renderType2 = ItemBlockRenderTypes.getChunkRenderType(blockState);
//                bufferBuilder2 = builderIn.builder(renderType2);
//                if (compiledChunkIn.hasLayer.add(renderType2)) {
//                    bufferBuilder2.begin(7, DefaultVertexFormat.BLOCK);
//                }
//
//                matrixstack.pushPose();
//                matrixstack.translate((double)(blockPos3.getX() & 15), (double)(blockPos3.getY() & 15), (double)(blockPos3.getZ() & 15));
//                if (blockRenderDispatcher.renderBatched(blockState, blockPos3, renderChunkRegion, matrixstack, bufferBuilder2, true, random)) {
//                    compiledChunkIn.isCompletelyEmpty = false;
//                    compiledChunkIn.hasBlocks.add(renderType2);
//                }
//
//                matrixstack.popPose();
//            }
//        }
    }

    public static void createBlockEntity(BlockGetter level, Map<BlockPos, BlockEntity> tileEntities, StructureTemplate.StructureBlockInfo info)
    {
        if (info.state.getBlock() instanceof EntityBlock)
        {
            tileEntities.put(info.pos, ((EntityBlock) info.state.getBlock()).newBlockEntity(level));
        }
    }
}
