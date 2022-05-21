package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.ITileDataService;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This provides a default implementation of the packet update methods.
 * <p>
 * <p>
 * Call {@link TileEntityDataBase#registerTileDataForNetwork(ITileData[])}
 * in the subclass' constructor to register tile data.
 */
public abstract class TileEntityDataBase
    extends TileDataContainerBase {

  protected final ITileDataService tileDataService;

  protected TileEntityDataBase(BlockEntityType<?> tileEntityType, BlockPos blockPos, BlockState blockState, ITileDataService tileDataService) {

    super(tileEntityType, blockPos, blockState);
    this.tileDataService = tileDataService;
  }

  // ---------------------------------------------------------------------------
  // - Network
  // ---------------------------------------------------------------------------

  protected void registerTileDataForNetwork(ITileData[] data) {

    this.tileDataService.register(this, data);
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void onTileDataUpdate() {
    //
  }

  @Nonnull
  @Override
  public CompoundTag getUpdateTag() {

    return this.serializeNBT();
  }

  @Nullable
  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {

    return ClientboundBlockEntityDataPacket.create(this);
  }
}