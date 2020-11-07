package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.common.network.SonarNetworkManager;
import io.github.ocelot.sonar.common.valuecontainer.SyncValueContainerMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class TestMessageHandler
{
    public static final String VERSION = "1.0";
    public static final SimpleChannel LOGIN = NetworkRegistry.newSimpleChannel(new ResourceLocation(TestMod.MOD_ID, "login"), () -> VERSION, VERSION::equals, VERSION::equals);
    public static final SimpleChannel PLAY = NetworkRegistry.newSimpleChannel(new ResourceLocation(TestMod.MOD_ID, "play"), () -> VERSION, VERSION::equals, VERSION::equals);

    private static final SonarNetworkManager LOGIN_NETWORK_MANAGER = new SonarNetworkManager(LOGIN, () -> TestClientHandler::new, () -> Object::new);
    private static final SonarNetworkManager PLAY_NETWORK_MANAGER = new SonarNetworkManager(PLAY, () -> TestClientHandler::new, () -> Object::new);

    public static void init()
    {
        LOGIN_NETWORK_MANAGER.registerLoginReply(TestLoginReplyMessage.class, TestLoginReplyMessage::new, NetworkDirection.LOGIN_TO_SERVER);
        LOGIN_NETWORK_MANAGER.registerLogin(TestLoginMessage.class, TestLoginMessage::new, NetworkDirection.LOGIN_TO_CLIENT);
        PLAY_NETWORK_MANAGER.register(SyncValueContainerMessage.class, SyncValueContainerMessage::new, NetworkDirection.PLAY_TO_SERVER);
        PLAY_NETWORK_MANAGER.register(CTestPlayMessage.class, CTestPlayMessage::new, NetworkDirection.PLAY_TO_SERVER);
        PLAY_NETWORK_MANAGER.register(STestPlayMessage.class, STestPlayMessage::new, NetworkDirection.PLAY_TO_CLIENT);
    }
}
