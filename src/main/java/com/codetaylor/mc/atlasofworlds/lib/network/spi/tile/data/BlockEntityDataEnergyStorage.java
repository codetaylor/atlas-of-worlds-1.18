package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.IBlockEntityDataEnergyStorage;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataBase;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.energy.IEnergyStorage;

import java.io.IOException;

public class BlockEntityDataEnergyStorage<T extends IEnergyStorage & IBlockEntityDataEnergyStorage>
    extends BlockEntityDataBase {

  private T energyStorage;

  public BlockEntityDataEnergyStorage(T energyStorage) {

    this(energyStorage, 1);
  }

  public BlockEntityDataEnergyStorage(T energyStorage, int updateInterval) {

    super(updateInterval);
    this.energyStorage = energyStorage;
    this.energyStorage.addObserver((handler, slot) -> this.setDirty(true));
    this.setDirty(true);
  }

  public IEnergyStorage getEnergyStorage() {

    return this.energyStorage;
  }

  @Override
  public void setDirty(boolean dirty) {

    super.setDirty(dirty);
  }

  @Override
  public void read(FriendlyByteBuf buffer) throws IOException {

    CompoundTag compound = Preconditions.checkNotNull(buffer.readNbt());
    this.energyStorage.deserializeNBT(compound);
  }

  @Override
  public void write(FriendlyByteBuf buffer) {

    buffer.writeNbt(this.energyStorage.serializeNBT());
  }
}
