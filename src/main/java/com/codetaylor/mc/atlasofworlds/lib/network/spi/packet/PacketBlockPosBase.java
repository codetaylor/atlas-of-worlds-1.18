package com.codetaylor.mc.atlasofworlds.lib.network.spi.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public abstract class PacketBlockPosBase<Q extends PacketBlockPosBase>
    implements IMessage<Q>,
    IMessageHandler<Q, IMessage> {

  protected BlockPos blockPos;

  public PacketBlockPosBase() {
    // serialization
  }

  public PacketBlockPosBase(BlockPos blockPos) {

    this.blockPos = blockPos;
  }

  @Override
  public Q decode(Q message, FriendlyByteBuf packetBuffer) {

    message.blockPos = packetBuffer.readBlockPos();
    return message;
  }

  @Override
  public void encode(Q message, FriendlyByteBuf packetBuffer) {

    packetBuffer.writeBlockPos(message.blockPos);
  }
}
