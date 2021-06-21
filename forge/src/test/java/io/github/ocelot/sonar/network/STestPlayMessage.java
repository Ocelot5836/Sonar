package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import net.minecraft.network.FriendlyByteBuf;

public class STestPlayMessage implements SonarMessage<Object>
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
        throw new RuntimeException("Client Exception from Server");
    }
}
