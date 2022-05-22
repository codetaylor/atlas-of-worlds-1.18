package com.codetaylor.mc.atlasofworlds.lib.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseContainer
    extends AbstractContainerMenu {

  protected static final int NO_SLOT_TARGET_INDEX = -1;
  protected static final int DEFAULT_HOTBAR_POSITION_X = 8;
  protected static final int DEFAULT_HOTBAR_POSITION_Y = 142;
  protected static final int DEFAULT_INVENTORY_POSITION_X = 8;
  protected static final int DEFAULT_INVENTORY_POSITION_Y = 84;
  protected static final int DEFAULT_SLOT_SPACING_X = 18;
  protected static final int DEFAULT_SLOT_SPACING_Y = 18;

  protected Inventory playerInventory;
  protected int nextSlotIndex;
  protected int playerInventorySlotIndexStart;
  protected int playerHotbarSlotIndexStart;

  public BaseContainer(@Nullable MenuType<?> type, int id, Inventory playerInventory) {

    super(type, id);

    this.playerInventory = playerInventory;
    this.nextSlotIndex = 0;
  }

  protected int containerPlayerInventoryAdd() {

    this.playerInventorySlotIndexStart = this.nextSlotIndex;

    for (int y = 0; y < 3; y++) {

      for (int x = 0; x < 9; x++) {
        int xPosition = containerInventoryPositionGetX() + x * this.containerSlotSpacingGetX();
        int yPosition = containerInventoryPositionGetY() + y * this.containerSlotSpacingGetY();
        this.addSlot(new Slot(this.playerInventory, x + y * 9 + 9, xPosition, yPosition));
      }

    }

    return this.nextSlotIndex - 1;
  }

  protected int containerPlayerHotbarAdd() {

    this.playerHotbarSlotIndexStart = this.nextSlotIndex;

    for (int x = 0; x < 9; x++) {
      int xPosition = this.containerHotbarPositionGetX() + this.containerSlotSpacingGetX() * x;
      int yPosition = this.containerHotbarPositionGetY();
      this.addSlot(new Slot(this.playerInventory, x, xPosition, yPosition));
    }

    return this.nextSlotIndex - 1;
  }

  protected int containerSlotAdd(Slot slot) {

    super.addSlot(slot);
    this.nextSlotIndex += 1;
    return this.nextSlotIndex - 1;
  }

  @Nonnull
  @Override
  protected Slot addSlot(@Nonnull Slot slot) {

    this.nextSlotIndex += 1;
    return super.addSlot(slot);
  }

  protected int containerSlotSpacingGetX() {

    return DEFAULT_SLOT_SPACING_X;
  }

  protected int containerSlotSpacingGetY() {

    return DEFAULT_SLOT_SPACING_Y;
  }

  protected int containerHotbarPositionGetX() {

    return DEFAULT_HOTBAR_POSITION_X;
  }

  protected int containerHotbarPositionGetY() {

    return DEFAULT_HOTBAR_POSITION_Y;
  }

  protected int containerInventoryPositionGetX() {

    return DEFAULT_INVENTORY_POSITION_X;
  }

  protected int containerInventoryPositionGetY() {

    return DEFAULT_INVENTORY_POSITION_Y;
  }

  protected boolean containerSlotIsPlayerInventory(int slotIndex) {

    return slotIndex >= this.playerInventorySlotIndexStart
        && slotIndex < this.playerInventorySlotIndexStart + 27;
  }

  protected boolean containerSlotIsPlayerHotbar(int slotIndex) {

    return slotIndex >= this.playerHotbarSlotIndexStart
        && slotIndex < this.playerHotbarSlotIndexStart + 9;
  }

  protected boolean containerShiftClickEnabled() {

    return true;
  }

  /**
   * This is called when the player is shift-clicking an ItemStack from
   * their inventory or hotbar and returns a slot index capable of
   * accepting the item. Returns a -1 if no slot can accept the item.
   * <p>
   * Use this if the container contains slots for things like machine
   * inputs.
   *
   * @param itemStack the item stack
   * @return a valid slot index or -1
   */
  @Nullable
  protected Slot containerItemStackSlotTargetGet(ItemStack itemStack) {

    return null;
  }

  @Nonnull
  @Override
  public ItemStack quickMoveStack(@Nonnull Player player, int slotIndex) {

    if (!this.containerShiftClickEnabled()) {
      return ItemStack.EMPTY;
    }

    ItemStack itemStackResult = ItemStack.EMPTY;
    Slot slot = this.slots.get(slotIndex);

    if (slot != null && slot.hasItem()) {

      ItemStack itemStack = slot.getItem();
      itemStackResult = itemStack.copy();

      if (!this.containerSlotItemStackMerge(slotIndex, itemStackResult, slot, itemStack)) {
        return ItemStack.EMPTY;
      }

      if (itemStack.getCount() <= 0) {
        slot.set(ItemStack.EMPTY);

      } else {
        slot.setChanged();
      }

      if (itemStack.getCount() == itemStackResult.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(player, itemStack);

    }

    return itemStackResult;
  }

  private boolean containerSlotItemStackMerge(int slotIndex, ItemStack itemStackResult, Slot slot, ItemStack itemStack) {

    // if the slot is not an inventory or hotbar slot
    if (!this.containerSlotIsPlayerInventory(slotIndex)
        && !this.containerSlotIsPlayerHotbar(slotIndex)) {

      // try to place the item in the player inventory or hotbar
      if (this.moveItemStackTo(itemStack, 0, 36, true)) {
        slot.onQuickCraft(itemStack, itemStackResult);
        return true;
      }

    } else { // the item is in the player's inventory or hotbar

      // ask the subclass for a target slot index
      Slot targetSlot = this.containerItemStackSlotTargetGet(itemStack);

      if (targetSlot != null) { // if subclass specified a slot

        int index = this.containerSlotIndexGet(targetSlot);

        if (index == NO_SLOT_TARGET_INDEX) {
          throw new RuntimeException();
        }

        // try to place in appropriate slot
        if (this.moveItemStackTo(itemStack, index, index + 1, false)) {
          return true;
        }

      } else { // subclass didn't specify a target slot

        if (this.containerSlotIsPlayerInventory(slotIndex)) { // item is in player's inventory

          // place in hotbar
          if (this.moveItemStackTo(itemStack, this.playerHotbarSlotIndexStart, this.playerHotbarSlotIndexStart + 9, false)) {
            return true;
          }

        } else { // item is in player's hotbar

          // place in inventory
          if (this.moveItemStackTo(itemStack, this.playerInventorySlotIndexStart, this.playerInventorySlotIndexStart + 27, false)) {
            return true;
          }

        }

      }

    }

    return false; // wasn't able to merge

  }

  private int containerSlotIndexGet(Slot targetSlot) {

    int index = NO_SLOT_TARGET_INDEX;

    for (int i = 0; i < this.slots.size(); i++) {

      if (targetSlot == this.slots.get(i)) {
        index = i;
        break;
      }

    }
    return index;
  }

}
