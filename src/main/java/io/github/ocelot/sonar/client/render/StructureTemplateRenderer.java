package io.github.ocelot.sonar.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import io.github.ocelot.sonar.common.util.OnlineRequest;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.NativeResource;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>Renders the blocks from a template the same way as the level renderer.</p>
 * <p>Make sure to call {@link StructureTemplateRenderer#free()} when the renderer is not needed anymore.</p>
 *
 * @author Ocelot
 * @since 6.1.0
 */
public class StructureTemplateRenderer implements NativeResource
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Supplier<CompletableFuture<StructureTemplate>> template;
    private final boolean constantAmbientLight;
    private final Function<LightChunkGetter, LevelLightEngine> lightManager;
    private final BiFunction<BlockPos, ColorResolver, Integer> colorResolver;
    private CompletableFuture<LoadedWorld> level;

    public StructureTemplateRenderer(Supplier<CompletableFuture<StructureTemplate>> template, boolean constantAmbientLight, Function<LightChunkGetter, LevelLightEngine> lightManager, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
    {
        this.template = template;
        this.constantAmbientLight = constantAmbientLight;
        this.lightManager = lightManager;
        this.colorResolver = colorResolver;
        this.reload();
    }

    public StructureTemplateRenderer(Supplier<CompletableFuture<StructureTemplate>> template, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
    {
        this(template, false, level -> new LevelLightEngine(level, true, true), colorResolver);
    }

    public StructureTemplateRenderer(CompletableFuture<StructureTemplate> template, boolean constantAmbientLight, Function<LightChunkGetter, LevelLightEngine> lightManager, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
    {
        this(() -> template, constantAmbientLight, lightManager, colorResolver);
    }

    public StructureTemplateRenderer(CompletableFuture<StructureTemplate> template, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
    {
        this(() -> template, false, level -> new LevelLightEngine(level, true, true), colorResolver);
    }

    public StructureTemplateRenderer(String templateLocation, boolean constantAmbientLight, Function<LightChunkGetter, LevelLightEngine> lightManager, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
    {
        this(() -> downloadTemplate(templateLocation), constantAmbientLight, lightManager, colorResolver);
    }

    public StructureTemplateRenderer(String templateLocation, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
    {
        this(() -> downloadTemplate(templateLocation), false, level -> new LevelLightEngine(level, true, true), colorResolver);
    }

    @SuppressWarnings("deprecation")
    private void renderBlockLayer(LoadedWorld level, RenderType blockLayerIn, PoseStack matrixStackIn, double cameraX, double cameraY, double cameraZ)
    {
        Minecraft minecraft = Minecraft.getInstance();
        blockLayerIn.setupRenderState();

        minecraft.getProfiler().push("filterempty");
        minecraft.getProfiler().popPush(() -> "render_" + blockLayerIn);

        VertexBuffer vertexbuffer = level.vertexBuffers.get(blockLayerIn);
        matrixStackIn.pushPose();
        matrixStackIn.translate(-cameraX, -cameraY, -cameraZ);
        vertexbuffer.bind();
        DefaultVertexFormat.BLOCK.setupBufferState(0L);
        vertexbuffer.draw(matrixStackIn.last().pose(), 7);
        matrixStackIn.popPose();

        VertexBuffer.unbind();
        RenderSystem.clearCurrentColor();
        DefaultVertexFormat.BLOCK.clearBufferState();
        minecraft.getProfiler().pop();
        blockLayerIn.clearRenderState();
    }

    /**
     * Renders the level relative to the camera position.
     *
     * @param matrixStack The stack of matrix transformations
     * @param cameraX     The x position of the camera
     * @param cameraY     The y position of the camera
     * @param cameraZ     The z position of the camera
     */
    @SuppressWarnings("deprecation")
    public void render(PoseStack matrixStack, double cameraX, double cameraY, double cameraZ)
    {
        LoadedWorld loadedWorld = this.level.getNow(null);
        if (loadedWorld == null)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.runAsFancy(() ->
        {
            this.renderBlockLayer(loadedWorld, RenderType.solid(), matrixStack, cameraX, cameraY, cameraZ);
            minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, minecraft.options.mipmapLevels > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
            this.renderBlockLayer(loadedWorld, RenderType.cutoutMipped(), matrixStack, cameraX, cameraY, cameraZ);
            minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).restoreLastBlurMipmap();
            this.renderBlockLayer(loadedWorld, RenderType.cutout(), matrixStack, cameraX, cameraY, cameraZ);
//            RenderHelper.setupLevelDiffuseLighting(matrixStack.getLast().getMatrix());
            this.renderBlockLayer(loadedWorld, RenderType.translucent(), matrixStack, cameraX, cameraY, cameraZ);
            minecraft.renderBuffers().bufferSource().endBatch();
        });
    }

    @Override
    public void free()
    {
        this.level.thenAcceptAsync(level ->
        {
            if (level == null)
                return;
            level.vertexBuffers.values().forEach(VertexBuffer::close);
        }, Util.backgroundExecutor());
    }

    /**
     * Reloads the current template if it has already been loaded.
     */
    public void reload()
    {
        if (this.level != null && !this.level.isDone())
            return;
        this.level = loadLevel(this.template.get(), this.constantAmbientLight, this.lightManager, this.colorResolver);
    }

    /**
     * @return A future of the level that will exist after it has loaded.
     */
    public CompletableFuture<? extends BlockAndTintGetter> getLevel()
    {
        return level;
    }

    /**
     * @return Whether the level loading has failed
     */
    public boolean hasFailed()
    {
        return this.level.isDone() && this.level.join() == null;
    }

    /**
     * @return The size of the template or {@link Vec3i#ZERO} if the level hasn't loaded yet.
     */
    public Vec3i getWorldSize()
    {
        return this.level.isDone() && this.level.join() != null ? this.level.join().getSize() : Vec3i.ZERO;
    }

    private static class LoadedWorld implements BlockAndTintGetter, LightChunkGetter
    {
        private final BiFunction<BlockPos, ColorResolver, Integer> colorResolver;
        private final boolean constantAmbientLight;
        private final LevelLightEngine lightManager;
        private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = new Object2ObjectArrayMap<>(3);
        private final Vec3i size;
        private final Map<Long, BlockState> blocks;
        private final Map<BlockPos, BlockEntity> tileEntities;
        private final Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap(it -> it, __ -> new VertexBuffer(DefaultVertexFormat.BLOCK)));
        private final CompletableFuture<?> completeFuture;

        private LoadedWorld(StructureTemplate template, boolean constantAmbientLight, Function<LightChunkGetter, LevelLightEngine> lightManager, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
        {
            this.colorResolver = colorResolver;
            this.constantAmbientLight = constantAmbientLight;
            this.lightManager = lightManager.apply(this);
            this.size = template.getSize();
            this.blocks = new HashMap<>();
            this.tileEntities = new HashMap<>();

            this.completeFuture = CompletableFuture.supplyAsync(() ->
            {
                Set<BlockPos> positions = new HashSet<>();
                for (StructureTemplate.StructureBlockInfo info : getTemplateBlocks(template))
                {
                    positions.add(info.pos);
                    this.blocks.put(info.pos.asLong(), info.state);
                    if (info.nbt != null)
                    {
                        if (info.state.hasTileEntity())
                        {
                            this.tileEntities.put(info.pos, info.state.createTileEntity(this));
                        }
                    }
                    this.lightManager.checkBlock(info.pos);
                }
                for (int x = 0; x < Math.ceil(template.getSize().getX() / 16F); x++)
                {
                    for (int y = 0; y < Math.ceil(template.getSize().getY() / 16F); y++)
                    {
                        for (int z = 0; z < Math.ceil(template.getSize().getZ() / 16F); z++)
                        {
                            this.lightManager.updateSectionStatus(SectionPos.of(x, y, z), false);
                            this.lightManager.enableLightSources(new ChunkPos(x, z), true);
                        }
                    }
                }
                for (BlockPos pos : positions)
                {
                    int light = this.getBlockState(pos).getLightValue(this, pos);
                    if (light > 0)
                        this.lightManager.onBlockEmissionIncrease(pos, light);
                }
                this.lightManager.runUpdates(Integer.MAX_VALUE, true, true);

                return positions;
            }, Util.backgroundExecutor()).thenAcceptAsync(positions ->
            {
                CompiledChunk compiledChunk = new CompiledChunk();
                this.compile(compiledChunk, positions);

                try
                {
                    ChunkBufferBuilderPack builder = Minecraft.getInstance().renderBuffers().fixedBufferPack();
                    if (compiledChunk.state != null && compiledChunk.layersUsed.contains(RenderType.translucent()))
                    {
                        BufferBuilder bufferbuilder = builder.builder(RenderType.translucent());
                        bufferbuilder.begin(7, DefaultVertexFormat.BLOCK);
                        bufferbuilder.restoreState(compiledChunk.state);
                        bufferbuilder.sortQuads(0, 0, 0);
                        compiledChunk.state = bufferbuilder.getState();
                        bufferbuilder.end();
                        this.vertexBuffers.get(RenderType.translucent()).uploadLater(builder.builder(RenderType.translucent())).join();
                    }
                    CompletableFuture.allOf(compiledChunk.layersStarted.stream().map(renderType -> this.vertexBuffers.get(renderType).uploadLater(builder.builder(renderType))).toArray(CompletableFuture[]::new)).join();
                }
                catch (Exception e)
                {
                    Minecraft.getInstance().delayCrash(CrashReport.forThrowable(e, "Rendering chunk"));
                }
            }, command -> RenderSystem.recordRenderCall(command::run));
        }

        @Override
        public float getShade(Direction direction, boolean shade)
        {
            if (!shade)
                return this.constantAmbientLight ? 0.9f : 1.0f;
            switch (direction)
            {
                case DOWN:
                    return this.constantAmbientLight ? 0.9f : 0.5f;
                case UP:
                    return this.constantAmbientLight ? 0.9f : 1.0f;
                case NORTH:
                case SOUTH:
                    return 0.8f;
                case WEST:
                case EAST:
                    return 0.6f;
            }
            return 1.0f;
        }

        @Override
        public LevelLightEngine getLightEngine()
        {
            return lightManager;
        }

        @Override
        public int getBlockTint(BlockPos pos, ColorResolver colorResolver)
        {
            return this.tintCaches.computeIfAbsent(colorResolver, key -> new BlockTintCache()).getColor(pos, () -> this.calculateBlockTint(pos, colorResolver));
        }

        private int calculateBlockTint(BlockPos pos, ColorResolver colorResolver)
        {
            int i = Minecraft.getInstance().options.biomeBlendRadius;
            if (i == 0)
            {
                return this.colorResolver.apply(pos, colorResolver);
            }
            else
            {
                int j = (i * 2 + 1) * (i * 2 + 1);
                int k = 0;
                int l = 0;
                int i1 = 0;
                Cursor3D cubecoordinateiterator = new Cursor3D(pos.getX() - i, pos.getY(), pos.getZ() - i, pos.getX() + i, pos.getY(), pos.getZ() + i);

                int j1;
                for (BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(); cubecoordinateiterator.advance(); i1 += j1 & 255)
                {
                    blockpos$mutable.set(cubecoordinateiterator.nextX(), cubecoordinateiterator.nextY(), cubecoordinateiterator.nextZ());
                    j1 = this.colorResolver.apply(blockpos$mutable, colorResolver);
                    k += (j1 & 16711680) >> 16;
                    l += (j1 & '\uff00') >> 8;
                }

                return (k / j & 255) << 16 | (l / j & 255) << 8 | i1 / j & 255;
            }
        }

        @Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos pos)
        {
            return this.tileEntities.get(pos);
        }

        @Override
        public BlockState getBlockState(BlockPos pos)
        {
            return this.blocks.getOrDefault(pos.asLong(), Blocks.AIR.defaultBlockState());
        }

        @Override
        public FluidState getFluidState(BlockPos pos)
        {
            return this.getBlockState(pos).getFluidState();
        }

        @Nullable
        @Override
        public BlockGetter getChunkForLighting(int chunkX, int chunkZ)
        {
            return this;
        }

        @Override
        public BlockGetter getLevel()
        {
            return this;
        }

        public Vec3i getSize()
        {
            return size;
        }

        private void compile(CompiledChunk compiledChunkIn, Set<BlockPos> positions)
        {
            ChunkBufferBuilderPack builderIn = Minecraft.getInstance().renderBuffers().fixedBufferPack();
            PoseStack matrixstack = new PoseStack();
            ModelBlockRenderer.enableCaching();
            Random random = new Random();
            BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();

            ItemBlockRenderTypes.setFancy(true);
            for (BlockPos blockpos2 : positions)
            {
                BlockState blockstate = this.getBlockState(blockpos2);

                FluidState fluidstate = this.getFluidState(blockpos2);
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
                        if (blockrendererdispatcher.renderLiquid(blockpos2, this, new LiquidVertexBuffer(bufferbuilder, matrixstack.last().pose(), matrixstack.last().normal()), fluidstate))
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
                        if (blockrendererdispatcher.renderModel(blockstate, blockpos2, this, matrixstack, bufferbuilder2, true, random, EmptyModelData.INSTANCE))
                        {
                            compiledChunkIn.layersUsed.add(rendertype);
                        }

                        matrixstack.popPose();
                    }
                }
            }
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);

            if (compiledChunkIn.layersUsed.contains(RenderType.translucent()))
            {
                BufferBuilder bufferbuilder1 = builderIn.builder(RenderType.translucent());
                bufferbuilder1.sortQuads(0, 0, 0);
                compiledChunkIn.state = bufferbuilder1.getState();
            }

            compiledChunkIn.layersStarted.stream().map(builderIn::builder).forEach(BufferBuilder::end);
            ModelBlockRenderer.clearCache();
        }
    }

    private static class LiquidVertexBuffer implements VertexConsumer
    {
        private final VertexConsumer delegate;
        private final Matrix4f position;
        private final Matrix3f normal;

        private LiquidVertexBuffer(VertexConsumer delegate, Matrix4f position, Matrix3f normal)
        {
            this.delegate = delegate;
            this.position = position;
            this.normal = normal;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z)
        {
            Vector4f vector4f = new Vector4f((float) x, (float) y, (float) z, 1.0F);
            vector4f.transform(this.position);
            this.delegate.vertex(vector4f.x(), vector4f.y(), vector4f.z());
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha)
        {
            this.delegate.color(red, green, blue, alpha);
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v)
        {
            this.delegate.uv(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v)
        {
            this.delegate.overlayCoords(u, v);
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v)
        {
            this.delegate.uv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z)
        {
            Vector3f vector3f = new Vector3f(x, y, z);
            vector3f.transform(this.normal);
            this.delegate.normal(vector3f.x(), vector3f.y(), vector3f.z());
            return this;
        }

        @Override
        public void endVertex()
        {
            this.delegate.endVertex();
        }
    }

    public static class CompiledChunk
    {
        private final Set<RenderType> layersUsed = new ObjectArraySet<>();
        private final Set<RenderType> layersStarted = new ObjectArraySet<>();
        @Nullable
        private BufferBuilder.State state;
    }

    private static CompletableFuture<StructureTemplate> downloadTemplate(String templateUrl)
    {
        return OnlineRequest.request(templateUrl, Util.backgroundExecutor()).thenApply(stream ->
        {
            try
            {
                CompoundTag nbt = NbtIo.readCompressed(stream);
                if (!nbt.contains("DataVersion", 99))
                    nbt.putInt("DataVersion", 500);
                StructureTemplate template = new StructureTemplate();
                template.load(NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, nbt, nbt.getInt("DataVersion")));
                return template;
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to load template from: " + templateUrl, e);
            }
            finally
            {
                IOUtils.closeQuietly(stream);
            }
            return null;
        });
    }

    private static CompletableFuture<LoadedWorld> loadLevel(CompletableFuture<StructureTemplate> templateFuture, boolean constantAmbientLight, Function<LightChunkGetter, LevelLightEngine> lightManager, BiFunction<BlockPos, ColorResolver, Integer> colorResolver)
    {
        return templateFuture.thenApplyAsync(template -> new LoadedWorld(template, constantAmbientLight, lightManager, colorResolver), Util.backgroundExecutor()).thenComposeAsync(level -> level.completeFuture.thenApplyAsync(__ -> level, Util.backgroundExecutor()), Util.backgroundExecutor()).exceptionally(e ->
        {
            LOGGER.error("Failed to load level template data", e);
            return null;
        });
    }

    @SuppressWarnings("deprecation")
    private static List<StructureTemplate.StructureBlockInfo> getTemplateBlocks(@Nullable StructureTemplate template)
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
}
