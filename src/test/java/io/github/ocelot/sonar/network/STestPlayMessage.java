package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.common.network.message.SonarMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STestPlayMessage implements SonarMessage<Object>
{
    @Override
    public void readPacketData(PacketBuffer buf)
    {
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
    }

    @Override
    public void processPacket(Object handler, NetworkEvent.Context ctx)
    {
        throw new RuntimeException("Client Exception from Server");
    }
}
