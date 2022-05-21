package com.codetaylor.mc.atlasofworlds.lib.inventory.spi;

import com.codetaylor.mc.atlasofworlds.lib.util.StackHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class LargeDynamicStackHandler
    extends DynamicStackHandler {

  public LargeDynamicStackHandler(int initialSize) {

    super(initialSize);
  }

  @Override
  public CompoundTag serializeNBT() {

    ListTag listTag = new ListTag();

    for (int i = 0; i < this.stacks.size(); i++) {

      if (!this.stacks.get(i).isEmpty()) {
        CompoundTag itemTag = StackHelper.writeLargeItemStack(this.stacks.get(i));
        itemTag.putInt("Slot", i);
        listTag.add(itemTag);
      }
    }

    CompoundTag tag = new CompoundTag();
    tag.put("Items", listTag);
    tag.putInt("Size", this.stacks.size());
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {

    setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : this.stacks.size());
    ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);

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
