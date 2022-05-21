package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.IBlockEntityDataItemStackHandler;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataBase;
import com.google.common.base.Preconditions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.io.IOException;
import java.util.BitSet;

public class BlockEntityDataItemStackHandler<H extends ItemStackHandler & IBlockEntityDataItemStackHandler>
    extends BlockEntityDataBase {

  private H stackHandler;
  private BitSet dirtySlots;

  public BlockEntityDataItemStackHandler(H stackHandler) {

    this(stackHandler, 1);
    this.dirtySlots = new BitSet(stackHandler.getSlots());
  }

  public BlockEntityDataItemStackHandler(H stackHandler, int updateInterval) {

    super(updateInterval);
    this.stackHandler = stackHandler;
    this.stackHandler.addObserver((handler, slot) -> {
      this.setDirty(true);
      this.dirtySlots.set(slot);
    });
    this.setDirty(true);
  }

  public ItemStackHandler getStackHandler() {

    return this.stackHandler;
  }

  @Override
  public void setDirty(boolean dirty) {

    super.setDirty(dirty);

    if (!dirty) {
      this.dirtySlots.clear();
    }
  }

  @Override
  public void read(FriendlyByteBuf buffer) throws IOException {

    int dirtyCount = buffer.readInt();

    for (int i = 0; i < dirtyCount; i++) {
      int slot = buffer.readInt();
      boolean clear = buffer.readBoolean();

      if (clear) {
        this.stackHandler.setStackInSlot(slot, ItemStack.EMPTY);

      } else {
        this.stackHandler.setStackInSlot(slot, this.readItemStack(buffer));
      }
    }
  }

  protected ItemStack readItemStack(FriendlyByteBuf buffer) throws IOException {

    return ItemStack.of(Preconditions.checkNotNull(buffer.readNbt()));
  }

  @Override
  public void write(FriendlyByteBuf buffer) {

    final int dirtyCount = this.dirtySlots.cardinality();

    buffer.writeInt(dirtyCount);

    if (dirtyCount > 0) {

      for (int i = this.dirtySlots.nextSetBit(0); i >= 0; i = this.dirtySlots.nextSetBit(i + 1)) {
        buffer.writeInt(i);
        ItemStack itemStack = this.stackHandler.getStackInSlot(i);

        buffer.writeBoolean(itemStack.isEmpty());

        if (!itemStack.isEmpty()) {
          this.writeItemStack(buffer, itemStack);
        }
      }
    }
  }

  protected void writeItemStack(FriendlyByteBuf buffer, ItemStack itemStack) {

    buffer.writeNbt(itemStack.serializeNBT());
  }

}
