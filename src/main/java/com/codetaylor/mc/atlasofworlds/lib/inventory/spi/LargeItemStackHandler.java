package com.codetaylor.mc.atlasofworlds.lib.inventory.spi;

import com.codetaylor.mc.atlasofworlds.lib.util.StackHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Serializes the stack count with an extra int in order to exceed the default
 * limitation of a byte: [-127,127].
 */
public class LargeItemStackHandler
    extends ItemStackHandler {

  public LargeItemStackHandler() {
    //
  }

  public LargeItemStackHandler(int size) {

    super(size);
  }

  public LargeItemStackHandler(NonNullList<ItemStack> stacks) {

    super(stacks);
  }

  @Override
  public CompoundTag serializeNBT() {

    ListTag nbtTagList = new ListTag();

    for (int i = 0; i < this.stacks.size(); i++) {

      if (!this.stacks.get(i).isEmpty()) {
        CompoundTag itemTag = StackHelper.writeLargeItemStack(this.stacks.get(i));
        itemTag.putInt("Slot", i);
        nbtTagList.add(itemTag);
      }
    }

    CompoundTag nbt = new CompoundTag();
    nbt.put("Items", nbtTagList);
    nbt.putInt("Size", this.stacks.size());
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {

    setSize(tag.contains("Size", Tag.TAG_INT) ? tag.getInt("Size") : this.stacks.size());
    ListTag tagList = tag.getList("Items", Tag.TAG_COMPOUND);

    for (int i = 0; i < tagList.size(); i++) {
      CompoundTag itemTags = tagList.getCompound(i);
      int slot = itemTags.getInt("Slot");

      if (slot >= 0 && slot < this.stacks.size()) {
        this.stacks.set(slot, StackHelper.readLargeItemStack(itemTags));
      }
    }
    this.onLoad();
  }
}
