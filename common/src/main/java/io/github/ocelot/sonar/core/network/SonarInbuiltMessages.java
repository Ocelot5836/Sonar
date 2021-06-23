package io.github.ocelot.sonar.core.network;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.common.network.SonarNetworkChannel;
import io.github.ocelot.sonar.common.network.message.SonarPacketDirection;
import io.github.ocelot.sonar.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 * @since 5.1.0
 */
@ApiStatus.Internal
public class SonarInbuiltMessages
{
    public static SonarNetworkChannel PLAY = SonarNetworkChannel.create(new ResourceLocation(Sonar.getParentModId(), Sonar.DOMAIN + "_play"), "1", () -> SonarInbuiltMessageClientHandler::new, () -> SonarInbuiltMessageServerHandler::new);

    public static void register()
    {
        PLAY.register(OpenValueContainerMessage.class, OpenValueContainerMessage::new, SonarPacketDirection.PLAY_CLIENTBOUND);
        PLAY.register(SyncValueContainerMessage.class, SyncValueContainerMessage::new, SonarPacketDirection.PLAY_SERVERBOUND);
    }
}
