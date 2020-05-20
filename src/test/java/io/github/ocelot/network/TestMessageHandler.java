package io.github.ocelot.network;

import io.github.ocelot.common.valuecontainer.SyncValueContainerMessage;
import io.github.ocelot.network.handler.ClientMessageHandler;
import io.github.ocelot.network.handler.MessageHandler;
import io.github.ocelot.network.handler.ServerMessageHandler;
import io.github.ocelot.TestMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestMessageHandler
{
    public static final String VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TestMod.MOD_ID, "instance"), () -> VERSION, VERSION::equals, VERSION::equals);

    private static int index;

    public static void init()
    {
        registerMessage(DisplayScreenMessage.class, DisplayScreenMessage::encode, DisplayScreenMessage::decode, (msg, ctx) -> getHandler(ctx).handleOpenGuiMessage(msg, ctx));
        registerMessage(SyncValueContainerMessage.class, SyncValueContainerMessage::encode, SyncValueContainerMessage::decode, (msg, ctx) -> getHandler(ctx).handleSyncValueContainerMessage(msg, ctx));
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
    {
        INSTANCE.registerMessage(index++, messageType, encoder, decoder, messageConsumer);
    }

    private static MessageHandler getHandler(Supplier<NetworkEvent.Context> ctx)
    {
        return ctx.get().getDirection().getReceptionSide().isClient() ? ClientMessageHandler.INSTANCE : ServerMessageHandler.INSTANCE;
    }
}
