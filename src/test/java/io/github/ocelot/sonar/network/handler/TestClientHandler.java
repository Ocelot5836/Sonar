package io.github.ocelot.sonar.network.handler;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.sonar.common.valuecontainer.IValueContainerClientHandler;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import io.github.ocelot.sonar.network.TestMessageHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class TestClientHandler implements IValueContainerClientHandler
{
    public TestClientHandler()
    {
        System.out.println("CREATING NEW CLIENT HANDLER");
    }

    @Nullable
    @Override
    public Screen createValueContainerScreen(ValueContainer container, BlockPos pos)
    {
        ResourceLocation textureLocation = new ResourceLocation(Sonar.DOMAIN, "textures/gui/value_container_editor.png");
        return new ValueContainerEditorScreenImpl(container, pos)
        {
            @Override
            public ResourceLocation getBackgroundTextureLocation()
            {
                return textureLocation;
            }

            @Override
            protected void sendDataToServer()
            {
                TestMessageHandler.PLAY.send(PacketDistributor.SERVER.noArg(), new SyncValueContainerMessage(this.getPos(), this.getEntries()));
            }
        };
    }
}
