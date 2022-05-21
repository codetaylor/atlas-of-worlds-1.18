package com.codetaylor.mc.atlasofworlds;

import net.minecraftforge.eventbus.api.IEventBus;

public interface ISidedProxy {

  void initialize();

  void registerModEventHandlers(IEventBus modEventBus);

  void registerForgeEventHandlers(IEventBus forgeEventBus);
}
