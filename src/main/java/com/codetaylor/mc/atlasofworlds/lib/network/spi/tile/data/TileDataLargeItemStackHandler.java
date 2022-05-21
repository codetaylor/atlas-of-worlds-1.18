package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.ITileDataItemStackHandler;
import com.codetaylor.mc.atlasofworlds.lib.util.StackHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.io.IOException;

/**
 * Normal stack serialization reduces the stack's count to a byte: [-127,127].
 * <p>
 * This supports syncing stack handler's with item stacks that have a count
 * larger than a byte. Specifically, uses an extra int during stack
 * serialization.
 *
 * @param <H>
 */
public class TileDataLargeItemStackHandler<H extends ItemStackHandler & ITileDataItemStackHandler>
    extends TileDataItemStackHandler<H> {

  public TileDataLargeItemStackHandler(H stackHandler) {

    super(stackHandler);
  }

  public TileDataLargeItemStackHandler(H stackHandler, int updateInterval) {

    super(stackHandler, updateInterval);
  }

  @Override
  protected ItemStack readItemStack(FriendlyByteBuf buffer) throws IOException {

    return StackHelper.readLargeItemStack(buffer.readNbt());
  }

  @Override
  protected void writeItemStack(FriendlyByteBuf buffer, ItemStack itemStack) {

    buffer.writeNbt(StackHelper.writeLargeItemStack(itemStack));
  }
}
