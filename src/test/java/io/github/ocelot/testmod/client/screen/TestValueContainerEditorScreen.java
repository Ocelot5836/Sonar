package io.github.ocelot.testmod.client.screen;

import io.github.ocelot.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.testmod.TestMod;
import io.github.ocelot.testmod.network.TestMessageHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class TestValueContainerEditorScreen extends ValueContainerEditorScreenImpl
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(TestMod.MOD_ID, "textures/gui/value_container_editor.png");

    public TestValueContainerEditorScreen(ValueContainer container)
    {
        super(container, () -> new StringTextComponent("Test Value Container Editor"));
    }

    @Override
    protected void sendDataToServer()
    {
        TestMessageHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(), new SyncValueContainerMessage(this.getContainer(), this.getEntries()));
    }

    @Override
    public ResourceLocation getBackgroundTextureLocation()
    {
        return TEXTURE;
    }
}
