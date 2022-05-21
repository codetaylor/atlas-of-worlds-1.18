package com.codetaylor.mc.atlasofworlds.lib.network.spi.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class ServerboundPacketBlockEntityBase<Q extends ServerboundPacketBlockEntityBase>
    extends PacketBlockPosBase<Q> {

  public ServerboundPacketBlockEntityBase() {
    // serialization
  }

  public ServerboundPacketBlockEntityBase(BlockPos blockPos) {

    super(blockPos);
  }

  @Override
  public IMessage onMessage(Q message, Supplier<NetworkEvent.Context> contextSupplier) {

    NetworkEvent.Context context = contextSupplier.get();
    ServerPlayer player = context.getSender();

    BlockEntity tileEntity = player.getLevel().getBlockEntity(message.blockPos);
    return this.onMessage(message, contextSupplier, tileEntity);
  }

  protected abstract IMessage onMessage(
      Q message,
      Supplier<NetworkEvent.Context> contextSupplier,
      BlockEntity tileEntity
  );
}
