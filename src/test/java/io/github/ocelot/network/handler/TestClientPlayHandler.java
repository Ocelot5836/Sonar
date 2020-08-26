package io.github.ocelot.network.handler;

import io.github.ocelot.TestMod;
import io.github.ocelot.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.network.TestMessageHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class TestClientPlayHandler implements ITestClientPlayHandler
{
    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(TestMod.MOD_ID, "textures/gui/value_container_editor.png");

    @Override
    public Screen createValueContainerScreen(ValueContainer container, BlockPos pos)
    {
        return new ValueContainerEditorScreenImpl(container, pos, () -> new StringTextComponent("Test Value Container Editor"))
        {
            @Override
            public ResourceLocation getBackgroundTextureLocation()
            {
                return TestClientPlayHandler.BACKGROUND_LOCATION;
            }

            @Override
            protected void sendDataToServer()
            {
                System.out.println("Packet Sent!");
                TestMessageHandler.PLAY.send(PacketDistributor.SERVER.noArg(), new SyncValueContainerMessage(this.getPos(), this.getEntries()));
            }
        };
    }
}
