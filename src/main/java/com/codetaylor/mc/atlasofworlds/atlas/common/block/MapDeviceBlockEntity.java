package com.codetaylor.mc.atlasofworlds.atlas.common.block;

import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.atlas.common.item.AtlasMapItem;
import com.codetaylor.mc.atlasofworlds.atlas.common.item.AtlasMapStone;
import com.codetaylor.mc.atlasofworlds.lib.inventory.spi.ObservableStackHandler;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityEntityDataBase;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.IBlockEntityData;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.IBlockEntityDataItemStackHandler;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.BlockEntityDataItemStackHandler;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class MapDeviceBlockEntity
    extends BlockEntityEntityDataBase {

  private final MapMatrixStackHandler mapMatrixStackHandler;
  private final StoneStackHandler stoneStackHandler;

  public MapDeviceBlockEntity(
      BlockPos blockPos,
      BlockState blockState,
      IBlockEntityDataService blockEntityDataService
  ) {

    super(AtlasModule.BlockEntityTypes.MAP_DEVICE, blockPos, blockState, blockEntityDataService);

    this.mapMatrixStackHandler = new MapMatrixStackHandler(2, 2);
    this.mapMatrixStackHandler.addObserver((stackHandler, slotIndex) -> this.setChanged());

    this.stoneStackHandler = new StoneStackHandler(6);
    this.stoneStackHandler.addObserver((stackHandler, slotIndex) -> this.setChanged());

    this.registerBlockEntityDataForNetwork(new IBlockEntityData[]{
        new BlockEntityDataItemStackHandler<>(this.mapMatrixStackHandler),
        new BlockEntityDataItemStackHandler<>(this.stoneStackHandler)
    });
  }

  // ---------------------------------------------------------------------------
  // Accessors
  // ---------------------------------------------------------------------------

  public MapMatrixStackHandler getMapMatrixStackHandler() {

    return this.mapMatrixStackHandler;
  }

  public StoneStackHandler getStoneStackHandler() {

    return this.stoneStackHandler;
  }

  // ---------------------------------------------------------------------------
  // Serialization
  // ---------------------------------------------------------------------------

  @Override
  public void load(@Nonnull CompoundTag tag) {

    super.load(tag);
    this.mapMatrixStackHandler.deserializeNBT(tag.getCompound("mapMatrixStackHandler"));
    this.stoneStackHandler.deserializeNBT(tag.getCompound("stoneStackHandler"));
  }

  @Override
  protected void saveAdditional(@Nonnull CompoundTag tag) {

    super.saveAdditional(tag);
    tag.put("mapMatrixStackHandler", this.mapMatrixStackHandler.serializeNBT());
    tag.put("stoneStackHandler", this.stoneStackHandler.serializeNBT());
  }

  // ---------------------------------------------------------------------------
  // Stack Handlers
  // ---------------------------------------------------------------------------

  public static class MapMatrixStackHandler
      extends ObservableStackHandler
      implements IItemHandlerModifiable,
      IBlockEntityDataItemStackHandler {

    private final int width;
    private final int height;

    public MapMatrixStackHandler(int width, int height) {

      super(width * height);
      this.width = width;
      this.height = height;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {

      return (stack.getItem() instanceof AtlasMapItem);
    }

    public int getWidth() {

      return this.width;
    }

    public int getHeight() {

      return this.height;
    }

    public boolean isEmpty() {

      int slotCount = this.width * this.height;

      for (int i = 0; i < slotCount; i++) {

        if (!this.getStackInSlot(i).isEmpty()) {
          return false;
        }
      }

      return true;
    }
  }

  public static class StoneStackHandler
      extends ObservableStackHandler
      implements IItemHandlerModifiable,
      IBlockEntityDataItemStackHandler {

    public StoneStackHandler(int size) {

      super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {

      return (stack.getItem() instanceof AtlasMapStone);
    }
  }
}
