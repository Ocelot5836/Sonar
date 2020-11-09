package io.github.ocelot.sonar.common.network.inbuilt;

import io.github.ocelot.sonar.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.sonar.common.valuecontainer.IValueContainerClientHandler;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

class SonarInbuiltMessageClientHandler implements IValueContainerClientHandler
{
    @Nullable
    @Override
    public Screen createValueContainerScreen(ValueContainer container, BlockPos pos)
    {
        return new ValueContainerEditorScreenImpl(container, pos)
        {
            @Override
            protected void sendDataToServer()
            {
                SonarInbuiltMessages.PLAY.send(PacketDistributor.SERVER.noArg(), new SyncValueContainerMessage(pos, this.getEntries()));
            }
        };
    }
}
