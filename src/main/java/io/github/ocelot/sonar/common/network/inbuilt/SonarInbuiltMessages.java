package io.github.ocelot.sonar.common.network.inbuilt;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.common.network.SonarNetworkManager;
import io.github.ocelot.sonar.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;

/**
 * <p>Handles automatically sending internal packets between the client and server.</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public class SonarInbuiltMessages
{
    public static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel PLAY = NetworkRegistry.newSimpleChannel(new ResourceLocation(Sonar.getParentModId(), Sonar.DOMAIN + "_play"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    @ApiStatus.Internal
    public static void register()
    {
        SonarNetworkManager networkManager = new SonarNetworkManager(PLAY, () -> SonarInbuiltMessageClientHandler::new, () -> SonarInbuiltMessageServerHandler::new);
        networkManager.register(OpenValueContainerMessage.class, OpenValueContainerMessage::new, NetworkDirection.PLAY_TO_CLIENT);
        networkManager.register(SyncValueContainerMessage.class, SyncValueContainerMessage::new, NetworkDirection.PLAY_TO_SERVER);
    }
}
