package io.github.ocelot.client.screen;

import io.github.ocelot.common.valuecontainer.ValueContainer;
import io.github.ocelot.TestMod;
import io.github.ocelot.network.TestMessageHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class TestValueContainerEditorScreen extends ValueContainerEditorScreenImpl
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(TestMod.MOD_ID, "textures/gui/value_container_editor.png");

    public TestValueContainerEditorScreen(ValueContainer container, BlockPos pos)
    {
        super(container, pos, () -> new StringTextComponent("Test Value Container Editor"));
    }

    @Override
    protected void sendDataToServer()
    {
        TestMessageHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(), this.createSyncMessage());
    }

    @Override
    public ResourceLocation getBackgroundTextureLocation()
    {
        return TEXTURE;
    }
}
