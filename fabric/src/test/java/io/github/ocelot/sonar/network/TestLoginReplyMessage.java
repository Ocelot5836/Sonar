package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.common.network.message.SimpleSonarLoginMessage;
import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import net.minecraft.network.FriendlyByteBuf;

public class TestLoginReplyMessage extends SimpleSonarLoginMessage<Object>
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
//        throw new RuntimeException("Lol");
    }
}
