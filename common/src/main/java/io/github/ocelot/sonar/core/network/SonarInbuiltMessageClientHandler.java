package io.github.ocelot.sonar.core.network;

import io.github.ocelot.sonar.client.screen.ValueContainerEditorScreenImpl;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainer;
import io.github.ocelot.sonar.common.valuecontainer.ValueContainerClientHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
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
                SonarInbuiltMessages.PLAY.sendToServer(new SyncValueContainerMessage(pos, this.getEntries()));
            }
        };
    }
}
