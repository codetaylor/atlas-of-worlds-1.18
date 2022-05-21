package com.codetaylor.mc.atlasofworlds.lib.network.spi.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class ClientboundPacketBlockEntityBase<Q extends ClientboundPacketBlockEntityBase<?>>
    extends PacketBlockPosBase<Q> {

  public ClientboundPacketBlockEntityBase() {
    // serialization
  }

  public ClientboundPacketBlockEntityBase(BlockPos blockPos) {

    super(blockPos);
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public IMessage onMessage(Q message, Supplier<NetworkEvent.Context> contextSupplier) {

    LocalPlayer player = Minecraft.getInstance().player;
    Level level = player.getLevel();

    if (level.isLoaded(message.blockPos)) {
      BlockEntity tileEntity = level.getBlockEntity(message.blockPos);
      return this.onMessage(message, contextSupplier, tileEntity);
    }

    return null;
  }

  protected abstract IMessage onMessage(
      Q message,
      Supplier<NetworkEvent.Context> contextSupplier,
      BlockEntity tileEntity
  );
}
