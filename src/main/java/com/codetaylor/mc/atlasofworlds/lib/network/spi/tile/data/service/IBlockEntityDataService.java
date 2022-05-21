package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service;

import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataTracker;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.IBlockEntityData;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataContainerBase;

import javax.annotation.Nullable;

public interface IBlockEntityDataService {

  int getServiceId();

  @Nullable
  BlockEntityDataTracker getTracker(BlockEntityDataContainerBase blockEntity);

  void register(BlockEntityDataContainerBase blockEntity, IBlockEntityData[] data);

  void update();
}
