package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.common.network.SonarNetworkChannel;
import io.github.ocelot.sonar.common.network.message.SonarPacketDirection;
import net.minecraft.resources.ResourceLocation;

public class TestMessageHandler
{
    public static final SonarNetworkChannel LOGIN = SonarNetworkChannel.create(new ResourceLocation(TestMod.MOD_ID, "login"), "1", () -> Object::new, () -> Object::new);
    public static final SonarNetworkChannel PLAY = SonarNetworkChannel.create(new ResourceLocation(TestMod.MOD_ID, "play"), "1", () -> Object::new, () -> Object::new);

    public static void init()
    {
        LOGIN.registerLoginReply(TestLoginReplyMessage.class, TestLoginReplyMessage::new, SonarPacketDirection.LOGIN_SERVERBOUND);
        LOGIN.registerLogin(TestLoginMessage.class, TestLoginMessage::new, SonarPacketDirection.LOGIN_CLIENTBOUND);
        PLAY.register(CTestPlayMessage.class, CTestPlayMessage::new, SonarPacketDirection.PLAY_SERVERBOUND);
        PLAY.register(STestPlayMessage.class, STestPlayMessage::new, SonarPacketDirection.PLAY_CLIENTBOUND);
    }
}
