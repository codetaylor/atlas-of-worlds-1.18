package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tile entities using the blockEntity data network service should extend this.
 */
public abstract class BlockEntityDataContainerBase
    extends BlockEntity
    implements IBlockEntityDataContainer {

  public BlockEntityDataContainerBase(BlockEntityType<?> blockEntityEntityType, BlockPos pos, BlockState blockState) {

    super(blockEntityEntityType, pos, blockState);
  }
}
