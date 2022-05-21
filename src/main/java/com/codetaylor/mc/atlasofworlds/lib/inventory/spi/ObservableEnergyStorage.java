package com.codetaylor.mc.atlasofworlds.lib.inventory.spi;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class ObservableEnergyStorage
    extends EnergyStorage
    implements IObservableEnergyStorage,
    INBTSerializable<Tag> {

  private List<IContentsChangedEventHandler> eventHandlerList;

  public ObservableEnergyStorage(int capacity) {

    this(capacity, capacity, capacity, 0);
  }

  public ObservableEnergyStorage(int capacity, int maxTransfer) {

    this(capacity, maxTransfer, maxTransfer, 0);
  }

  public ObservableEnergyStorage(int capacity, int maxReceive, int maxExtract) {

    this(capacity, maxReceive, maxExtract, 0);
  }

  public ObservableEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {

    super(capacity, maxReceive, maxExtract, energy);
    this.eventHandlerList = new ArrayList<>();
  }

  @Override
  public void addObserver(IContentsChangedEventHandler handler) {

    this.eventHandlerList.add(handler);
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {

    if (!this.canReceive()) {
      return 0;
    }

    int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));

    if (!simulate) {
      this.energy += energyReceived;
      this.onContentsChanged(energyReceived);
    }

    return energyReceived;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {

    if (!canExtract()) {
      return 0;
    }

    int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));

    if (!simulate) {
      this.energy -= energyExtracted;
      this.onContentsChanged(-energyExtracted);
    }

    return energyExtracted;
  }

  @Override
  public CompoundTag serializeNBT() {

    CompoundTag compound = new CompoundTag();
    compound.putInt("energy", this.energy);
    return compound;
  }

  @Override
  public void deserializeNBT(Tag tag) {

    this.energy = Math.min(this.capacity, ((CompoundTag) tag).getInt("energy"));
  }

  protected void onContentsChanged(int amount) {

    if (amount != 0
        && !this.eventHandlerList.isEmpty()) {

      for (IContentsChangedEventHandler handler : this.eventHandlerList) {
        handler.onContentsChanged(this, amount);
      }
    }
  }
}
