package com.codetaylor.mc.atlasofworlds.lib.network.spi.packet;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessageHandler<Q, A extends IMessage> {

  A onMessage(Q message, Supplier<NetworkEvent.Context> contextSupplier);
}
