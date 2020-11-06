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
        CACHE.getTextureLocation("https://cdn.discordapp.com/attachments/426584849088774187/773706957239484426/image0.jpg");
        CACHE.getTextureLocation("https://cdn.discordapp.com/attachments/426584849088774187/773706924775702548/image0.png");
        CACHE.getTextureLocation("https://cdn.discordapp.com/attachments/426584849088774187/773627554082979850/spyglass_scope.png");
        CACHE.getTextureLocation("https://cdn.discordapp.com/attachments/426584849088774187/773616627710820363/unknown.png");
    }
}