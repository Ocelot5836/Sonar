package io.github.ocelot.sonar.client;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.client.render.TestTileEntityRenderer;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@SuppressWarnings("deprecation")
public class TestClientInit
{
    public static void init()
    {
        DeferredWorkQueue.runLater(() -> ClientRegistry.bindTileEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new));

        RenderingRegistry.registerEntityRenderingHandler(TestMod.TEST_ENTITY_A.get(), BeeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TestMod.TEST_ENTITY_B.get(), BeeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TestMod.TEST_ENTITY_C.get(), BeeRenderer::new);
    }
}
