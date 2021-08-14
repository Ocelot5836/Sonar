package io.github.ocelot.sonar.network;

import io.github.ocelot.sonar.common.network.message.SimpleSonarLoginMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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
    public void processPacket(Object handler, NetworkEvent.Context ctx)
    {
        TestMessageHandler.LOGIN.reply(new TestLoginReplyMessage(), ctx);
    }
}
