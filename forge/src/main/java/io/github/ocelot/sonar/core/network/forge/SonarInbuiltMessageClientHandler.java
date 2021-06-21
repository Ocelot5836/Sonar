package io.github.ocelot.sonar.core.network.forge;

import io.github.ocelot.sonar.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainerClientHandler;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import io.github.ocelot.sonar.core.network.forge.SonarInbuiltMessagesImpl;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

class SonarInbuiltMessageClientHandler implements ValueContainerClientHandler
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
                SonarInbuiltMessagesImpl.PLAY.send(PacketDistributor.SERVER.noArg(), new SyncValueContainerMessage(pos, this.getEntries()));
            }
        };
    }
}
