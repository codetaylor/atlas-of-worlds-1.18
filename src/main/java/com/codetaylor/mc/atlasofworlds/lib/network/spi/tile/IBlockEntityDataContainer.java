package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBlockEntityDataContainer {

  /**
   * Called on the client when a TE receives a data update packet.
   * <p>
   * You can check the dirty flag on the TE's data objects during this call.
   * All updated data will be flagged dirty for the duration of this call.
   */
  @OnlyIn(Dist.CLIENT)
  void onTileDataUpdate();
}
