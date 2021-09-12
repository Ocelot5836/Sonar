package io.github.ocelot.sonar.common.network.inbuilt;

import io.github.ocelot.sonar.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.sonar.common.valuecontainer.IValueContainerClientHandler;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

/**
 * @deprecated TODO remove in 7.0.0
 */
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
