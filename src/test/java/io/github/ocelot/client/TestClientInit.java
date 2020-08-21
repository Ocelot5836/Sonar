package io.github.ocelot.client;

import io.github.ocelot.TestMod;
import io.github.ocelot.client.render.TestTileEntityRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class TestClientInit
{
    public static void init()
    {
        ClientRegistry.bindTileEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new);
    }
}
