package io.github.ocelot.network;

import io.github.ocelot.TestMod;
import io.github.ocelot.common.valuecontainer.DefaultValueContainerClientFunctionality;
import io.github.ocelot.common.valuecontainer.DefaultValueContainerServerFunctionality;
import io.github.ocelot.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.common.valuecontainer.SyncValueContainerMessage;
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
        registerMessage(OpenValueContainerMessage.class, OpenValueContainerMessage::encode, OpenValueContainerMessage::decode, DefaultValueContainerClientFunctionality::handleOpenGuiMessage);
        registerMessage(SyncValueContainerMessage.class, SyncValueContainerMessage::encode, SyncValueContainerMessage::decode, DefaultValueContainerServerFunctionality::handleSyncValueContainerMessage);
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
    {
        INSTANCE.registerMessage(index++, messageType, encoder, decoder, messageConsumer);
    }
}
