package com.codetaylor.mc.atlasofworlds.lib.inventory.spi;

import net.minecraftforge.energy.IEnergyStorage;

public interface IObservableEnergyStorage {

  void addObserver(IContentsChangedEventHandler handler);

  interface IContentsChangedEventHandler {

    void onContentsChanged(IEnergyStorage energyStorage, int amount);
  }
}
