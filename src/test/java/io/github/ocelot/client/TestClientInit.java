package io.github.ocelot.client;

import io.github.ocelot.TestMod;
import io.github.ocelot.client.render.TestTileEntityRenderer;
import io.github.ocelot.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.common.valuecontainer.DefaultValueContainerClientFunctionality;
import io.github.ocelot.network.TestMessageHandler;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class TestClientInit
{
    public static void init()
    {
        ClientRegistry.bindTileEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new);
        DefaultValueContainerClientFunctionality.setScreenFactory((container, pos) -> new ValueContainerEditorScreenImpl(container, pos, () -> new StringTextComponent("Test Value Container Editor"))
        {
            @Override
            protected void sendDataToServer()
            {
                DefaultValueContainerClientFunctionality.sendDataToServer(TestMessageHandler.INSTANCE, this);
            }
        });
    }
}
