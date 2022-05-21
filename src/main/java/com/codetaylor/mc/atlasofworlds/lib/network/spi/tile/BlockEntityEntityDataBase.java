package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
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
 * Call {@link BlockEntityEntityDataBase#registerTileDataForNetwork(IBlockEntityData[])}
 * in the subclass' constructor to register blockEntity data.
 */
public abstract class BlockEntityEntityDataBase
    extends BlockEntityDataContainerBase {

  protected final IBlockEntityDataService blockEntityDataService;

  protected BlockEntityEntityDataBase(BlockEntityType<?> blockEntityEntityType, BlockPos blockPos, BlockState blockState, IBlockEntityDataService blockEntityDataService) {

    super(blockEntityEntityType, blockPos, blockState);
    this.blockEntityDataService = blockEntityDataService;
  }

  // ---------------------------------------------------------------------------
  // - Network
  // ---------------------------------------------------------------------------

  protected void registerTileDataForNetwork(IBlockEntityData[] data) {

    this.blockEntityDataService.register(this, data);
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