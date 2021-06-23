package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.common.network.message.SimpleSonarLoginMessage;
import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import net.minecraft.network.FriendlyByteBuf;

public class TestLoginMessage extends SimpleSonarLoginMessage<Object>
{
    @Override
    public void readPacketData(FriendlyByteBuf buf)
    {
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf)
    {
    }

    @Override
    public void processPacket(Object handler, SonarNetworkContext ctx)
    {
//        TestMessageHandler.LOGIN.reply(new TestLoginReplyMessage(), ctx);
        TestMessageHandler.LOGIN.reply(new TestLoginReplyMessage(), ctx);
    }
}
