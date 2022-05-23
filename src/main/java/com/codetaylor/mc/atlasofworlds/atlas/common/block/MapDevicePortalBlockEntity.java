package com.codetaylor.mc.atlasofworlds.atlas.common.block;

import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityEntityDataBase;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MapDevicePortalBlockEntity
    extends BlockEntityEntityDataBase {

  public MapDevicePortalBlockEntity(
      BlockPos blockPos,
      BlockState blockState,
      IBlockEntityDataService blockEntityDataService
  ) {

    super(AtlasModule.BlockEntityTypes.MAP_DEVICE, blockPos, blockState, blockEntityDataService);
  }

}
