package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile;

import com.codetaylor.mc.atlasofworlds.lib.inventory.spi.IObservableEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Energy storage data elements need to implement the
 * {@link IObservableEnergyStorage} interface.
 */
public interface IBlockEntityDataEnergyStorage
    extends IObservableEnergyStorage,
    INBTSerializable<CompoundTag> {

}
