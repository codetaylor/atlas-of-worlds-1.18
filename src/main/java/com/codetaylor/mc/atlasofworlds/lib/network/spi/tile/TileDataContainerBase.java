package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tile entities using the tile data network service should extend this.
 */
public abstract class TileDataContainerBase
    extends BlockEntity
    implements ITileDataContainer {

  public TileDataContainerBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState blockState) {

    super(tileEntityType, pos, blockState);
  }
}
