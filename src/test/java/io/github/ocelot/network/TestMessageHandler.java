package io.github.ocelot.network;

import io.github.ocelot.TestMod;
import io.github.ocelot.common.network.FishNetworkManager;
import io.github.ocelot.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.network.handler.TestClientLoginHandler;
import io.github.ocelot.network.handler.TestClientPlayHandler;
import io.github.ocelot.network.handler.TestServerLoginHandler;
import io.github.ocelot.network.handler.TestServerPlayHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class TestMessageHandler
{
    public static final String VERSION = "1.0";
    public static final SimpleChannel LOGIN = NetworkRegistry.newSimpleChannel(new ResourceLocation(TestMod.MOD_ID, "login"), () -> VERSION, VERSION::equals, VERSION::equals);
    public static final SimpleChannel PLAY = NetworkRegistry.newSimpleChannel(new ResourceLocation(TestMod.MOD_ID, "play"), () -> VERSION, VERSION::equals, VERSION::equals);

    private static final FishNetworkManager LOGIN_NETWORK_MANAGER = new FishNetworkManager(LOGIN, () -> TestClientLoginHandler::new, () -> TestServerLoginHandler::new);
    private static final FishNetworkManager PLAY_NETWORK_MANAGER = new FishNetworkManager(PLAY, () -> TestClientPlayHandler::new, () -> TestServerPlayHandler::new);

    public static void init()
    {
        LOGIN_NETWORK_MANAGER.registerLoginReply(TestLoginReplyMessage.class, TestLoginReplyMessage::new, NetworkDirection.LOGIN_TO_CLIENT);
        LOGIN_NETWORK_MANAGER.registerLogin(TestLoginMessage.class, TestLoginMessage::new, NetworkDirection.LOGIN_TO_CLIENT);
        PLAY_NETWORK_MANAGER.register(SyncValueContainerMessage.class, SyncValueContainerMessage::new, NetworkDirection.PLAY_TO_SERVER);
        PLAY_NETWORK_MANAGER.register(SyncValueContainerMessage.class, SyncValueContainerMessage::new, NetworkDirection.PLAY_TO_SERVER);
    }
}
