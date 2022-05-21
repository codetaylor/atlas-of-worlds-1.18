package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataBase;
import net.minecraft.network.FriendlyByteBuf;

public class BlockEntityDataInteger
    extends BlockEntityDataBase {

  private int value;

  public BlockEntityDataInteger(int initialValue) {

    this(initialValue, 1);
  }

  public BlockEntityDataInteger(int initialValue, int updateInterval) {

    super(updateInterval);
    this.set(initialValue);
  }

  public void set(int value) {

    if (value != this.value) {
      this.value = value;
      this.setDirty(true);
    }
  }

  public int get() {

    return this.value;
  }

  public int add(int value) {

    this.set(this.value + value);
    return this.value;
  }

  @Override
  public void read(FriendlyByteBuf buffer) {

    this.value = buffer.readInt();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {

    buffer.writeInt(this.value);
  }

}
