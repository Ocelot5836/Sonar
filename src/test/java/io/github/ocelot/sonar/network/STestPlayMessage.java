package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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
    public void processPacket(Object handler, NetworkEvent.Context ctx)
    {
        throw new RuntimeException("Client Exception from Server");
    }
}
