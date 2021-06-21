package io.github.ocelot.sonar.core.network.forge;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.common.network.forge.SonarNetworkManager;
import io.github.ocelot.sonar.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * @author Ocelot
 * @since 5.1.0
 */
public class SonarInbuiltMessagesImpl
{
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel PLAY = NetworkRegistry.newSimpleChannel(new ResourceLocation(Sonar.getParentModId(), Sonar.DOMAIN + "_play"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register()
    {
        SonarNetworkManager networkManager = new SonarNetworkManager(PLAY, () -> SonarInbuiltMessageClientHandler::new, () -> SonarInbuiltMessageServerHandler::new);
        networkManager.register(OpenValueContainerMessage.class, OpenValueContainerMessage::new, NetworkDirection.PLAY_TO_CLIENT);
        networkManager.register(SyncValueContainerMessage.class, SyncValueContainerMessage::new, NetworkDirection.PLAY_TO_SERVER);
    }
}
