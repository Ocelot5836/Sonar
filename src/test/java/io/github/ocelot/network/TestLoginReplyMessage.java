package io.github.ocelot.network;

import io.github.ocelot.common.network.message.FishLoginMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class TestLoginReplyMessage implements FishLoginMessage<Object>
{
    private int loginIndex;

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
    }

    @Override
    public int getAsInt()
    {
        return loginIndex;
    }

    @Override
    public void setLoginIndex(int index)
    {
        this.loginIndex = index;
    }
}
