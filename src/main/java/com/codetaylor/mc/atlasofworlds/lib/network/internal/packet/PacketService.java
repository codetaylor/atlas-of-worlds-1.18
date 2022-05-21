package com.codetaylor.mc.atlasofworlds.lib.network.internal.packet;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IMessage;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IMessageHandler;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketService
    implements IPacketService {

  public static final int DEFAULT_RANGE = 64;

  private final SimpleChannel channel;

  private int nextRegistrationIndex;

  public static PacketService create(String modId, String channelName, String protocolVersion) {

    ResourceLocation name = new ResourceLocation(modId, channelName);
    Supplier<String> protocolVersionSupplier = () -> protocolVersion;
    SimpleChannel simpleChannel = NetworkRegistry.newSimpleChannel(name, protocolVersionSupplier, protocolVersion::equals, protocolVersion::equals);
    return new PacketService(simpleChannel);
  }

  private PacketService(SimpleChannel channel) {

    this.channel = channel;
  }

  @Override
  public <Q extends IMessage, A extends IMessage> void registerMessage(
      Class<? extends IMessageHandler<Q, A>> messageHandler,
      Class<Q> requestMessageType
  ) {

    this.registerMessage(
        this.instantiateHandler(messageHandler),
        this.instantiateMessage(requestMessageType),
        this.nextRegistrationIndex
    );

    this.nextRegistrationIndex += 1;
  }

  private <Q extends IMessage> Q instantiateMessage(Class<Q> messageType) {

    try {
      return messageType.newInstance();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private <Q extends IMessage, A extends IMessage> IMessageHandler<? super Q, ? extends A> instantiateHandler(
      Class<? extends IMessageHandler<? super Q, ? extends A>> handler
  ) {

    try {
      return handler.newInstance();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private <Q extends IMessage, A extends IMessage> void registerMessage(
      IMessageHandler<? super Q, ? extends A> messageHandler,
      Q message,
      int id
  ) {

    IMessageHandler<Q, A> wrapper = new ThreadedMessageReplyHandler(this, messageHandler);
    BiConsumer<Q, Supplier<NetworkEvent.Context>> handlerSupplier = wrapper::onMessage;
    BiConsumer<Q, FriendlyByteBuf> encoder = message::encode;
    Class<Q> messageClass = (Class<Q>) message.getClass();
    Function<FriendlyByteBuf, Q> decoder = packetBuffer -> {
      Q m = this.instantiateMessage(messageClass);
      //noinspection unchecked
      return (Q) m.decode(m, packetBuffer);
    };
    this.channel.registerMessage(id, messageClass, encoder, decoder, handlerSupplier);
  }

  /**
   * Convenience method to send a packet to all entities tracking the chunk that contains the given block entity.
   *
   * @param blockEntity
   * @param message
   */
  @Override
  public void sendToTrackingChunk(BlockEntity blockEntity, IMessage message) {

    BlockPos pos = blockEntity.getBlockPos();
    Level level = blockEntity.getLevel();

    if (level != null) {
      LevelChunk chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
      this.sendToTrackingChunk(chunk, message);
    }
  }

  @Override
  public void sendToPlayer(ServerPlayer player, IMessage message) {

    this.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
  }

  @Override
  public void sendToDimension(ResourceKey<Level> dimensionType, IMessage message) {

    this.channel.send(PacketDistributor.DIMENSION.with(() -> dimensionType), message);
  }

  @Override
  public void sendToNear(PacketDistributor.TargetPoint targetPoint, IMessage message) {

    this.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), message);
  }

  @Override
  public void sendToNear(BlockEntity blockEntity, IMessage message) {

    Level level = blockEntity.getLevel();

    ResourceKey<Level> dimensionKey = Objects.requireNonNull(level).dimension();
    BlockPos blockPos = blockEntity.getBlockPos();
    this.sendToNear(new PacketDistributor.TargetPoint(blockPos.getX(), blockPos.getY(), blockPos.getZ(), DEFAULT_RANGE, dimensionKey), message);
  }

  @Override
  public void sendToAll(IMessage message) {

    this.channel.send(PacketDistributor.ALL.noArg(), message);
  }

  @Override
  public void sendToServer(IMessage message) {

    this.channel.send(PacketDistributor.SERVER.noArg(), message);
  }

  @Override
  public void sendToTrackingEntity(Entity entity, IMessage message) {

    this.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
  }

  @Override
  public void sendToTrackingEntityAndSelf(Entity entity, IMessage message) {

    this.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
  }

  @Override
  public void sendToTrackingChunk(LevelChunk chunk, IMessage message) {

    this.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
  }

}