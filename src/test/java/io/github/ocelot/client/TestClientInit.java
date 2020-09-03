package io.github.ocelot.client;

import io.github.ocelot.TestMod;
import io.github.ocelot.client.render.TestTileEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@SuppressWarnings("deprecation")
public class TestClientInit
{
    public static void init()
    {
        DeferredWorkQueue.runLater(() ->
        {
            ClientRegistry.bindTileEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new);

            EntityRendererManager rendererManager = Minecraft.getInstance().getRenderManager();
            BeeRenderer renderer = new BeeRenderer(rendererManager);
            rendererManager.register(TestMod.TEST_ENTITY_A.get(), renderer);
            rendererManager.register(TestMod.TEST_ENTITY_B.get(), renderer);
            rendererManager.register(TestMod.TEST_ENTITY_C.get(), renderer);
        });
    }
}
