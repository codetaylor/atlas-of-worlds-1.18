package com.codetaylor.mc.atlasofworlds.lib.network.spi.packet;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

public interface IPacketService {

  <Q extends IMessage, A extends IMessage> void registerMessage(
      Class<? extends IMessageHandler<Q, A>> messageHandler,
      Class<Q> requestMessageType
  );

  void sendToTrackingChunk(BlockEntity blockEntity, IMessage message);

  void sendToDimension(ResourceKey<Level> dimension, IMessage message);

  void sendToPlayer(ServerPlayer player, IMessage message);

  void sendToNear(PacketDistributor.TargetPoint targetPoint, IMessage message);

  void sendToNear(BlockEntity blockEntity, IMessage message);

  void sendToTrackingEntity(Entity entity, IMessage message);

  void sendToTrackingEntityAndSelf(Entity entity, IMessage message);

  void sendToTrackingChunk(LevelChunk chunk, IMessage message);

  void sendToAll(IMessage message);

  void sendToServer(IMessage message);
}