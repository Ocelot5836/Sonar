package io.github.ocelot.network;

import io.github.ocelot.TestMod;
import io.github.ocelot.common.network.FishNetworkManager;
import io.github.ocelot.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.network.handler.TestClientPlayerHandler;
import io.github.ocelot.network.handler.TestServerPlayHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class TestMessageHandler
{
    public static final String VERSION = "1.0";
    public static final SimpleChannel PLAY = NetworkRegistry.newSimpleChannel(new ResourceLocation(TestMod.MOD_ID, "play"), () -> VERSION, VERSION::equals, VERSION::equals);
    
    private static final FishNetworkManager PLAY_NETWORK_MANAGER = new FishNetworkManager(PLAY, () -> TestClientPlayerHandler::new, () -> TestServerPlayHandler::new);

    public static void init()
    {
        PLAY_NETWORK_MANAGER.register(OpenValueContainerMessage.class, OpenValueContainerMessage::new, NetworkDirection.PLAY_TO_CLIENT);
        PLAY_NETWORK_MANAGER.register(SyncValueContainerMessage.class, SyncValueContainerMessage::new, NetworkDirection.PLAY_TO_SERVER);
    }
}
