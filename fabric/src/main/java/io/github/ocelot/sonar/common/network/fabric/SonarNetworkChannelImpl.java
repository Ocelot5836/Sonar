package io.github.ocelot.sonar.common.network.fabric;

import io.github.ocelot.sonar.common.network.SonarNetworkChannel;
import io.github.ocelot.sonar.common.network.message.SonarLoginMessage;
import io.github.ocelot.sonar.common.network.message.SonarMessage;
import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import io.github.ocelot.sonar.common.network.message.SonarPacketDirection;
import io.github.ocelot.sonar.core.network.FabricSonarNetworkContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class SonarNetworkChannelImpl implements SonarNetworkChannel
{
    private final ResourceLocation channelId;
    private final List<PacketFactory> factories;
    private final LazyLoadedValue<LazyLoadedValue<Object>> clientMessageHandler;
    private final LazyLoadedValue<LazyLoadedValue<Object>> serverMessageHandler;

    private SonarNetworkChannelImpl(ResourceLocation channelId, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory)
    {
        this.channelId = channelId;
        this.factories = new ArrayList<>();
        this.clientMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(clientFactory.get()));
        this.serverMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(serverFactory.get()));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ClientPlayNetworking.registerGlobalReceiver(this.channelId, this::processClient);
        ServerPlayNetworking.registerGlobalReceiver(this.channelId, this::processServer);
    }

    private void processClient(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf data, PacketSender packetSender)
    {
        SonarNetworkChannel.processMessage(this.deserialize(data), new FabricSonarNetworkContext(listener.getConnection(), SonarPacketDirection.PLAY_CLIENTBOUND), this.clientMessageHandler.get().get());
    }

    private void processServer(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, FriendlyByteBuf data, PacketSender responseSender)
    {
        SonarNetworkChannel.processMessage(this.deserialize(data), new FabricSonarNetworkContext(listener.getConnection(), SonarPacketDirection.PLAY_SERVERBOUND), this.serverMessageHandler.get().get());
    }

    @Override
    public void reply(SonarMessage<?> message, SonarNetworkContext context)
    {
        switch (context.getDirection())
        {
            case PLAY_SERVERBOUND:
                context.getNetworkManager().send(ServerPlayNetworking.createS2CPacket(this.channelId, this.serialize(message)));
                break;
            case PLAY_CLIENTBOUND:
                context.getNetworkManager().send(ClientPlayNetworking.createC2SPacket(this.channelId, this.serialize(message)));
                break;
        }
    }

    @Override
    public void sendTo(ServerPlayer player, SonarMessage<?> message)
    {
        ServerPlayNetworking.send(player, this.channelId, this.serialize(message));
    }

    @Override
    public void sendTo(ServerLevel level, SonarMessage<?> message)
    {
        FriendlyByteBuf data = this.serialize(message);
        for (ServerPlayer player : PlayerLookup.world(level))
            ServerPlayNetworking.send(player, this.channelId, data);
    }

    @Override
    public void sendToNear(ServerLevel level, double x, double y, double z, double radius, SonarMessage<?> message)
    {
        FriendlyByteBuf data = this.serialize(message);
        for (ServerPlayer player : PlayerLookup.around(level, new Vec3(x, y, z), radius))
            ServerPlayNetworking.send(player, this.channelId, data);
    }

    @Override
    public void sendToAll(MinecraftServer server, SonarMessage<?> message)
    {
        FriendlyByteBuf data = this.serialize(message);
        for (ServerPlayer player : PlayerLookup.all(server))
            ServerPlayNetworking.send(player, this.channelId, data);
    }

    @Override
    public void sendToServer(SonarMessage<?> message)
    {
        ClientPlayNetworking.send(this.channelId, this.serialize(message));
    }

    @Override
    public void sendToTracking(Entity entity, SonarMessage<?> message)
    {
        FriendlyByteBuf data = this.serialize(message);
        for (ServerPlayer player : PlayerLookup.tracking(entity))
            ServerPlayNetworking.send(player, this.channelId, data);
    }

    @Override
    public void sendToTracking(ServerLevel level, BlockPos pos, SonarMessage<?> message)
    {
        FriendlyByteBuf data = this.serialize(message);
        for (ServerPlayer player : PlayerLookup.tracking(level, pos))
            ServerPlayNetworking.send(player, this.channelId, data);
    }

    @Override
    public void sendToTracking(ServerLevel level, ChunkPos pos, SonarMessage<?> message)
    {
        FriendlyByteBuf data = this.serialize(message);
        for (ServerPlayer player : PlayerLookup.tracking(level, pos))
            ServerPlayNetworking.send(player, this.channelId, data);
    }

    @Override
    public void sendToTrackingAndSelf(Entity entity, SonarMessage<?> message)
    {
        FriendlyByteBuf data = this.serialize(message);
        if (entity instanceof ServerPlayer)
            ServerPlayNetworking.send((ServerPlayer) entity, this.channelId, data);
        for (ServerPlayer player : PlayerLookup.tracking(entity))
            ServerPlayNetworking.send(player, this.channelId, data);
    }

    private FriendlyByteBuf serialize(SonarMessage<?> message)
    {
        Optional<PacketFactory> factoryOptional = this.factories.stream().filter(factory -> factory.clazz == message.getClass()).findFirst();
        if (!factoryOptional.isPresent())
            throw new IllegalStateException("Unregistered packet: " + message.getClass() + " on channel: " + this.channelId);

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(this.factories.indexOf(factoryOptional.get()));
        message.writePacketData(buf);
        return buf;
    }

    private SonarMessage<?> deserialize(FriendlyByteBuf buf)
    {
        int id = buf.readVarInt();
        if (id < 0 || id >= this.factories.size())
            throw new IllegalStateException("Unknown packet with id: " + id);

        PacketFactory factory = this.factories.get(id);
        SonarMessage<?> packet = factory.generator.get();
        packet.readPacketData(buf);
        return packet;
    }

    @Override
    public <MSG extends SonarMessage<T>, T> void register(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction)
    {
    }

    @Override
    public <MSG extends SonarLoginMessage<T>, T> void registerLoginReply(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction)
    {
        throw new UnsupportedOperationException("Login replies are not supported");
    }

    @Override
    public <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction)
    {
        throw new UnsupportedOperationException("Login packets are not supported");
    }

    @Override
    public <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable SonarPacketDirection direction)
    {
        throw new UnsupportedOperationException("Auto sent login packets are not supported");
    }

    public static SonarNetworkChannel create(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory)
    {
        return new SonarNetworkChannelImpl(channelId, clientFactory, serverFactory);
    }

    private static class PacketFactory
    {
        private final Class<?> clazz;
        private final Supplier<SonarMessage<?>> generator;

        private PacketFactory(Class<?> clazz, Supplier<SonarMessage<?>> generator)
        {
            this.clazz = clazz;
            this.generator = generator;
        }
    }
}
