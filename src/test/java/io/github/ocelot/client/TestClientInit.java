package io.github.ocelot.client;

import io.github.ocelot.TestMod;
import io.github.ocelot.client.render.TestTileEntityRenderer;
import io.github.ocelot.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.common.valuecontainer.DefaultValueContainerClientFunctionality;
import io.github.ocelot.network.TestMessageHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.PacketDistributor;

public class TestClientInit
{
    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("examplemod", "textures/gui/value_container_editor.png");

    public static void init()
    {
        ClientRegistry.bindTileEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new);
        DefaultValueContainerClientFunctionality.setScreenFactory((container, pos) -> new ValueContainerEditorScreenImpl(container, pos, () -> new StringTextComponent("Test Value Container Editor"))
        {
            @Override
            public ResourceLocation getBackgroundTextureLocation()
            {
                return TestClientInit.BACKGROUND_LOCATION;
            }

            @Override
            protected void sendDataToServer()
            {
                TestMessageHandler.PLAY.send(PacketDistributor.SERVER.noArg(), this.createSyncMessage());
            }
        });
    }
}
