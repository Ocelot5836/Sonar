package io.github.ocelot.sonar.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ocelot.sonar.client.util.OnlineImageCache;
import io.github.ocelot.sonar.tileentity.TestTileEntity;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class TestTileEntityRenderer implements BlockEntityRenderer<TestTileEntity>
{
    private static final OnlineImageCache CACHE = new OnlineImageCache(10, TimeUnit.SECONDS);

    @Override
    public void render(TestTileEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
    }
}