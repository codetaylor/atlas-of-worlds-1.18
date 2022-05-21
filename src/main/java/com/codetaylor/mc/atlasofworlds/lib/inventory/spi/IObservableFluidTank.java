package com.codetaylor.mc.atlasofworlds.lib.inventory.spi;

import net.minecraftforge.fluids.capability.templates.FluidTank;

public interface IObservableFluidTank {

  void addObserver(IContentsChangedEventHandler handler);

  interface IContentsChangedEventHandler {

    void onContentsChanged(FluidTank fluidTank, int amount);
  }
}
