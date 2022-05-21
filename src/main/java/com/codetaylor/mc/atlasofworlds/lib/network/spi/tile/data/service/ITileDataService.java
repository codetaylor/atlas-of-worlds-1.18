package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service;

import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataTracker;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.ITileData;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.TileDataContainerBase;

import javax.annotation.Nullable;

public interface ITileDataService {

  int getServiceId();

  @Nullable
  BlockEntityDataTracker getTracker(TileDataContainerBase tile);

  void register(TileDataContainerBase tile, ITileData[] data);

  void update();
}
