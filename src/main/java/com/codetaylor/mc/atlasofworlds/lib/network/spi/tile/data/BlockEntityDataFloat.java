package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataBase;
import com.codetaylor.mc.atlasofworlds.lib.util.MathConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class BlockEntityDataFloat
    extends BlockEntityDataBase {

  private float value;

  public BlockEntityDataFloat(float initialValue) {

    this(initialValue, 1);
  }

  public BlockEntityDataFloat(float initialValue, int updateInterval) {

    super(updateInterval);
    this.set(initialValue);
  }

  public void set(float value) {

    if (Mth.abs(value - this.value) > MathConstants.FLT_EPSILON) {
      this.value = value;
      this.setDirty(true);
    }
  }

  public float get() {

    return this.value;
  }

  public float add(float value) {

    this.set(this.value + value);
    return this.value;
  }

  @Override
  public void read(FriendlyByteBuf buffer) {

    this.value = buffer.readFloat();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {

    buffer.writeFloat(this.value);
  }

}
