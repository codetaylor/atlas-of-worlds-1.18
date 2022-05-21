package com.codetaylor.mc.atlasofworlds.lib.network.spi.packet;

import net.minecraft.network.FriendlyByteBuf;

public interface IMessage<T extends IMessage> {

  void encode(T message, FriendlyByteBuf packetBuffer);

  T decode(T message, FriendlyByteBuf packetBuffer);
}
