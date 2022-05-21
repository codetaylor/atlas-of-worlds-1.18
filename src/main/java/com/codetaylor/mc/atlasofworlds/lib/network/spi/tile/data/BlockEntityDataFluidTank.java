package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.IBlockEntityDataFluidTank;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataBase;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.io.IOException;

public class BlockEntityDataFluidTank<T extends FluidTank & IBlockEntityDataFluidTank>
    extends BlockEntityDataBase {

  private T fluidTank;

  public BlockEntityDataFluidTank(T fluidTank) {

    this(fluidTank, 1);
  }

  public BlockEntityDataFluidTank(T fluidTank, int updateInterval) {

    super(updateInterval);
    this.fluidTank = fluidTank;
    this.fluidTank.addObserver((handler, slot) -> this.setDirty(true));
    this.setDirty(true);
  }

  public FluidTank getFluidTank() {

    return this.fluidTank;
  }

  @Override
  public void setDirty(boolean dirty) {

    super.setDirty(dirty);
  }

  @Override
  public void read(FriendlyByteBuf buffer) throws IOException {

    this.fluidTank.readFromNBT(Preconditions.checkNotNull(buffer.readNbt()));
  }

  @Override
  public void write(FriendlyByteBuf buffer) {

    buffer.writeNbt(this.fluidTank.writeToNBT(new CompoundTag()));
  }
}
