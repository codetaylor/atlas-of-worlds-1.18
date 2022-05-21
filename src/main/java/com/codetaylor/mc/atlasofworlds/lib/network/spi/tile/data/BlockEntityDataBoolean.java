package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataBase;
import net.minecraft.network.FriendlyByteBuf;

public class BlockEntityDataBoolean
    extends BlockEntityDataBase {

  private boolean value;

  public BlockEntityDataBoolean(boolean initialValue) {

    this(initialValue, 1);
  }

  public BlockEntityDataBoolean(boolean initialValue, int updateInterval) {

    super(updateInterval);
    this.set(initialValue);
  }

  public void set(boolean value) {

    if (value != this.value) {
      this.value = value;
      this.setDirty(true);
    }
  }

  public boolean get() {

    return this.value;
  }

  @Override
  public void read(FriendlyByteBuf buffer) {

    this.value = buffer.readBoolean();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {

    buffer.writeBoolean(this.value);
  }

}
