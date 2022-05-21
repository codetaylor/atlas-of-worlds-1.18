package com.codetaylor.mc.atlasofworlds.lib.inventory.spi;

import net.minecraftforge.items.ItemStackHandler;

public interface IObservableStackHandler {

  void addObserver(IContentsChangedEventHandler handler);

  interface IContentsChangedEventHandler {

    void onContentsChanged(ItemStackHandler stackHandler, int slotIndex);
  }
}
