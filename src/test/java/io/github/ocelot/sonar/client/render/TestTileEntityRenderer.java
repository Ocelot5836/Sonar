package io.github.ocelot.sonar.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.ocelot.sonar.client.util.OnlineImageCache;
import io.github.ocelot.sonar.tileentity.TestTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import java.util.concurrent.TimeUnit;

public class TestTileEntityRenderer extends TileEntityRenderer<TestTileEntity>
{
    private static final OnlineImageCache CACHE = new OnlineImageCache(10, TimeUnit.SECONDS);

    public TestTileEntityRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(TestTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
    }
}