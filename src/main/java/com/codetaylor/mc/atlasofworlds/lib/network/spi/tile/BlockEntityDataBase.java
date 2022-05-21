package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base blockEntity data element.
 * <p>
 * It implements the default, expected behavior of blockEntity data elements and all
 * blockEntity data elements should extend it.
 */
public abstract class BlockEntityDataBase
    implements IBlockEntityData {

  public interface IChangeObserver<D extends BlockEntityDataBase> {

    void onDirtyStateChanged(D data);

    class OnDirtyMarkTileDirty<D extends BlockEntityDataBase>
        implements IChangeObserver<D> {

      private final BlockEntity blockEntity;

      public OnDirtyMarkTileDirty(BlockEntity blockEntity) {

        this.blockEntity = blockEntity;
      }

      @Override
      public void onDirtyStateChanged(D data) {

        if (data.isDirty()) {
          this.blockEntity.setChanged();
        }
      }
    }
  }

  private final int updateInterval;
  private int updateCounter;
  private boolean dirty;
  private boolean forceUpdate;
  private List<IChangeObserver> changeObservers;

  protected BlockEntityDataBase(int updateInterval) {

    this.updateInterval = updateInterval;
  }

  public void addChangeObserver(IChangeObserver observer) {

    if (this.changeObservers == null) {
      this.changeObservers = new ArrayList<>(1);
    }

    this.changeObservers.add(observer);
  }

  @Override
  public void setDirty(boolean dirty) {

    boolean changed = (this.dirty != dirty);

    this.dirty = dirty;

    if (this.changeObservers != null
        && changed) {
      //noinspection ForLoopReplaceableByForEach
      for (int i = 0; i < this.changeObservers.size(); i++) {
        //noinspection unchecked
        this.changeObservers.get(i).onDirtyStateChanged(this);
      }
    }
  }

  @Override
  public boolean isDirty() {

    return this.dirty && (this.updateCounter == 0);
  }

  @Override
  public void forceUpdate() {

    this.forceUpdate = true;
  }

  @Override
  public void update() {

    if (this.forceUpdate) {
      this.updateCounter = 0;
      this.forceUpdate = false;

    } else {
      this.updateCounter += 1;

      if (this.updateCounter >= this.updateInterval) {
        this.updateCounter = 0;
      }
    }
  }
}
