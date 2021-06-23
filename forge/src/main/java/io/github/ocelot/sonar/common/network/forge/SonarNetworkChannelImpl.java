package io.github.ocelot.sonar.common.network.forge;

import io.github.ocelot.sonar.common.network.SonarNetworkChannel;
import io.github.ocelot.sonar.common.network.message.SonarLoginMessage;
import io.github.ocelot.sonar.common.network.message.SonarMessage;
import io.github.ocelot.sonar.common.network.message.SonarNetworkContext;
import io.github.ocelot.sonar.common.network.message.SonarPacketDirection;
import io.github.ocelot.sonar.core.network.ForgeSonarNetworkContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class SonarNetworkChannelImpl implements SonarNetworkChannel
{
    private final SimpleChannel channel;
    private final LazyLoadedValue<LazyLoadedValue<Object>> clientMessageHandler;
    private final LazyLoadedValue<LazyLoadedValue<Object>> serverMessageHandler;
    private int nextId;

    private SonarNetworkChannelImpl(SimpleChannel channel, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory)
    {
        this.channel = channel;
        this.clientMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(clientFactory.get()));
        this.serverMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(serverFactory.get()));
    }

    private <MSG extends SonarMessage<T>, T> SimpleChannel.MessageBuilder<MSG> getMessageBuilder(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction)
    {
        return this.channel.messageBuilder(clazz, this.nextId++, toNetworkDirection(direction)).encoder(SonarMessage::writePacketData).decoder(buf ->
        {
            MSG msg = generator.get();
            msg.readPacketData(buf);
            return msg;
        }).consumer((msg, ctx) ->
        {
            SonarNetworkChannel.processMessage(msg, new ForgeSonarNetworkContext(ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler.get().get() : this.serverMessageHandler.get().get());
            ctx.get().setPacketHandled(true);
        });
    }

    @Override
    public void reply(SonarMessage<?> message, SonarNetworkContext context)
    {
        this.channel.reply(message, ((ForgeSonarNetworkContext) context).getContext());
    }

    @Override
    public void sendTo(ServerPlayer player, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    @Override
    public void sendTo(ServerLevel level, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    }

    @Override
    public void sendToNear(ServerLevel level, double x, double y, double z, double radius, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius * radius, level.dimension())), message);
    }

    @Override
    public void sendToAll(MinecraftServer server, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.ALL.noArg(), message);
    }

    @Override
    public void sendToServer(SonarMessage<?> message)
    {
        this.channel.sendToServer(message);
    }

    @Override
    public void sendToTracking(Entity entity, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    @Override
    public void sendToTracking(ServerLevel level, BlockPos pos, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), message);
    }

    @Override
    public void sendToTracking(ServerLevel level, ChunkPos pos, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(pos.x, pos.z)), message);
    }

    @Override
    public void sendToTrackingAndSelf(Entity entity, SonarMessage<?> message)
    {
        this.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    @Override
    public <MSG extends SonarMessage<T>, T> void register(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction)
    {
        this.getMessageBuilder(clazz, generator, direction).add();
    }

    @Override
    public <MSG extends SonarLoginMessage<T>, T> void registerLoginReply(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction)
    {
        this.channel.messageBuilder(clazz, this.nextId++, toNetworkDirection(direction)).encoder(SonarMessage::writePacketData).decoder(buf ->
        {
            MSG msg = generator.get();
            msg.readPacketData(buf);
            return msg;
        })
                .consumer(FMLHandshakeHandler.indexFirst((__, msg, ctx) ->
                {
                    SonarNetworkChannel.processMessage(msg, new ForgeSonarNetworkContext(ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler.get().get() : this.serverMessageHandler.get().get());
                    ctx.get().setPacketHandled(true);
                }))
                .loginIndex(SonarLoginMessage::getAsInt, SonarLoginMessage::setLoginIndex)
                .add();
    }

    @Override
    public <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, @Nullable SonarPacketDirection direction)
    {
        this.getMessageBuilder(clazz, generator, direction)
                .loginIndex(SonarLoginMessage::getAsInt, SonarLoginMessage::setLoginIndex)
                .markAsLoginPacket()
                .add();
    }

    @Override
    public <MSG extends SonarLoginMessage<T>, T> void registerLogin(Class<MSG> clazz, Supplier<MSG> generator, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable SonarPacketDirection direction)
    {
        this.getMessageBuilder(clazz, generator, direction)
                .loginIndex(SonarLoginMessage::getAsInt, SonarLoginMessage::setLoginIndex)
                .buildLoginPacketList(loginPacketGenerators)
                .add();
    }

    private static NetworkDirection toNetworkDirection(@Nullable SonarPacketDirection direction)
    {
        if (direction == null)
            return null;
        switch (direction)
        {
            case PLAY_SERVERBOUND:
                return NetworkDirection.PLAY_TO_SERVER;
            case PLAY_CLIENTBOUND:
                return NetworkDirection.PLAY_TO_CLIENT;
            case LOGIN_SERVERBOUND:
                return NetworkDirection.LOGIN_TO_SERVER;
            case LOGIN_CLIENTBOUND:
                return NetworkDirection.LOGIN_TO_CLIENT;
            default:
                throw new IllegalStateException("Unknown network direction: " + direction);
        }
    }

    public static SonarNetworkChannel create(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory)
    {
        return new SonarNetworkChannelImpl(NetworkRegistry.newSimpleChannel(channelId, () -> version, version::equals, version::equals), clientFactory, serverFactory);
    }
}
