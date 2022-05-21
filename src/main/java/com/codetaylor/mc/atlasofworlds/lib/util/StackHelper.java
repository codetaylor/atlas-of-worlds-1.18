package com.codetaylor.mc.atlasofworlds.lib.util;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public final class StackHelper {

  public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";

  public static boolean isFuel(ItemStack itemStack) {

    return FurnaceBlockEntity.isFuel(itemStack);
  }

  public static int getItemBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {

    return ForgeHooks.getBurnTime(itemStack, recipeType);
  }

  /**
   * Decreases the stack in the given slot by the given amount.
   *
   * @param stackHandler   the handler
   * @param slot           the slot to adjust
   * @param amount         the amount to add
   * @param checkContainer should this decrement be container sensitive
   * @return the adjusted stack
   * @see #decrease(ItemStack, int, boolean)
   */
  public static ItemStack decreaseStackInSlot(ItemStackHandler stackHandler, int slot, int amount, boolean checkContainer) {

    ItemStack stackInSlot = stackHandler.getStackInSlot(slot).copy();
    ItemStack adjustedStack = StackHelper.decrease(stackInSlot, amount, checkContainer);
    stackHandler.setStackInSlot(slot, adjustedStack);
    return adjustedStack;
  }

  /**
   * Returns an item stack's {@link CompoundTag}. If the stack is empty,
   * returns a new, empty tag.
   *
   * @param itemStack the item stack
   * @return the stack's tag or a new tag if the stack is empty
   */
  @Nonnull
  public static CompoundTag getTagSafe(ItemStack itemStack) {

    if (itemStack.isEmpty()) {
      return new CompoundTag();
    }

    CompoundTag tag = itemStack.getTag();

    if (tag == null) {
      tag = new CompoundTag();
      itemStack.setTag(tag);
    }

    return tag;
  }

  @ParametersAreNonnullByDefault
  public static List<ItemStack> copyInto(List<ItemStack> sourceList, List<ItemStack> targetList) {

    for (ItemStack itemStack : sourceList) {
      targetList.add(itemStack.copy());
    }

    return targetList;
  }

  /**
   * Container sensitive decrease stack.
   * <p>
   * ie. bucket
   *
   * @param itemStack      the {@link ItemStack}
   * @param amount         decrease amount
   * @param checkContainer check for container
   * @return the resulting {@link ItemStack}
   */
  public static ItemStack decrease(ItemStack itemStack, int amount, boolean checkContainer) {

    if (itemStack.isEmpty()) {
      return ItemStack.EMPTY;
    }

    itemStack.shrink(amount);

    if (itemStack.getCount() <= 0) {

      if (checkContainer && itemStack.getItem().hasContainerItem(itemStack)) {
        return itemStack.getItem().getContainerItem(itemStack);

      } else {
        return ItemStack.EMPTY;
      }
    }

    return itemStack;
  }

  /**
   * Must be called from both sides, client and server.
   */
  public static void addToInventoryOrSpawn(Level level, Player player, ItemStack itemStack, BlockPos pos, double offsetY, boolean preferActiveSlot, boolean playPickupSound) {

    if (preferActiveSlot) {
      IItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());

      ItemStack remainder = inventory.insertItem(player.getInventory().selected, itemStack, false);

      if (!remainder.isEmpty()) {
        remainder = ItemHandlerHelper.insertItemStacked(inventory, remainder, false);
      }

      if (playPickupSound
          && remainder.isEmpty()
          || remainder.getCount() != itemStack.getCount()) {

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F
        );
      }

      if (!remainder.isEmpty() && !level.isClientSide) {
        StackHelper.spawnStackOnTop(level, itemStack, pos, offsetY);
      }

    } else {

      if (!player.getInventory().add(itemStack)) {

        if (!level.isClientSide) {
          StackHelper.spawnStackOnTop(level, itemStack, pos, offsetY);
        }

      } else if (playPickupSound) {
        level.playSound(player, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F
        );
      }
    }
  }

  /**
   * Iterates the contents of an {@link ItemStackHandler} and spawns each
   * non-empty item in the world.
   * <p>
   * Server only.
   *
   * @param level        the level
   * @param stackHandler the stack handler to empty
   * @param pos          the position to spawn the items
   */
  public static void spawnStackHandlerContentsOnTop(Level level, ItemStackHandler stackHandler, BlockPos pos) {

    StackHelper.spawnStackHandlerContentsOnTop(level, stackHandler, pos, 1.0);
  }

  /**
   * Iterates the contents of an {@link ItemStackHandler} and spawns each
   * non-empty item in the world.
   * <p>
   * Server only.
   *
   * @param level        the world
   * @param stackHandler the stack handler to empty
   * @param pos          the position to spawn the items
   * @param offsetY      the Y offset to spawn the items
   */
  public static void spawnStackHandlerContentsOnTop(Level level, ItemStackHandler stackHandler, BlockPos pos, double offsetY) {

    for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
      StackHelper.spawnStackHandlerSlotContentsOnTop(level, stackHandler, slot, pos, offsetY);
    }
  }

  public static void spawnStackHandlerSlotContentsOnTop(Level level, ItemStackHandler stackHandler, int slot, BlockPos pos) {

    StackHelper.spawnStackHandlerSlotContentsOnTop(level, stackHandler, slot, pos, 1.0);
  }

  public static void spawnStackHandlerSlotContentsOnTop(Level level, ItemStackHandler stackHandler, int slot, BlockPos pos, double offsetY) {

    ItemStack itemStack;

    while (!(itemStack = stackHandler.extractItem(slot, stackHandler.getSlotLimit(slot), false)).isEmpty()) {
      StackHelper.spawnStackOnTop(level, itemStack, pos, offsetY);
    }
  }

  /**
   * Spawns an {@link ItemStack} in the world, directly above the given position.
   * <p>
   * Server only.
   *
   * @param level     the world
   * @param itemStack the {@link ItemStack} to spawn
   * @param pos       the position to spawn
   */
  public static void spawnStackOnTop(Level level, ItemStack itemStack, BlockPos pos) {

    StackHelper.spawnStackOnTop(level, itemStack, pos, 1.0);
  }

  /**
   * Spawns an {@link ItemStack} in the world, directly above the given position.
   * <p>
   * Server only.
   *
   * @param level     the world
   * @param itemStack the {@link ItemStack} to spawn
   * @param pos       the position to spawn
   */
  public static void spawnStackOnTop(Level level, ItemStack itemStack, BlockPos pos, double offsetY) {

    ItemEntity entityItem = new ItemEntity(
        level,
        pos.getX() + 0.5,
        pos.getY() + 0.5 + offsetY,
        pos.getZ() + 0.5,
        itemStack
    );
    entityItem.setDeltaMovement(0, 0.1, 0);

    level.addFreshEntity(entityItem);
  }

  /**
   * Create and write a tile entity's NBT to the block entity tag of an item stack.
   *
   * @param block       the block
   * @param amount      the amount
   * @param blockEntity the block entity
   * @return the IS
   */
  public static ItemStack createItemStackFromTileEntity(Block block, int amount, BlockEntity blockEntity) {

    return StackHelper.createItemStackFromTileEntity(Item.byBlock(block), amount, blockEntity);
  }

  /**
   * Create and write a tile entity's NBT to the block entity tag of an item stack.
   *
   * @param item        the item
   * @param amount      the amount
   * @param blockEntity the block entity
   * @return the IS
   */
  public static ItemStack createItemStackFromTileEntity(Item item, int amount, BlockEntity blockEntity) {

    ItemStack itemStack = new ItemStack(() -> item, amount);
    return StackHelper.writeTileEntityToItemStack(blockEntity, itemStack);
  }

  /**
   * Write a tile entity's NBT to the block entity tag of an item stack.
   *
   * @param blockEntity the block entity
   * @param itemStack   the IS
   * @return the IS
   */
  public static ItemStack writeTileEntityToItemStack(BlockEntity blockEntity, ItemStack itemStack) {

    CompoundTag compound;

    if (itemStack.getTag() != null) {
      compound = itemStack.getTag();

    } else {
      compound = new CompoundTag();
    }

    CompoundTag teCompound = blockEntity.serializeNBT();
    compound.put(BLOCK_ENTITY_TAG, teCompound);
    itemStack.setTag(compound);
    return itemStack;
  }

  public static <T extends BlockEntity> T readTileEntityFromItemStack(T tile, ItemStack itemStack) {

    CompoundTag tagCompound = itemStack.getTag();

    if (tagCompound != null) {
      CompoundTag tileCompound = tagCompound.getCompound(BLOCK_ENTITY_TAG);
      tile.deserializeNBT(tileCompound);
    }

    return tile;
  }

  public static ItemStack readLargeItemStack(CompoundTag compound) {

    ItemStack itemStack = ItemStack.of(Preconditions.checkNotNull(compound));
    itemStack.setCount(compound.getInt("CountLarge"));
    return itemStack;
  }

  public static CompoundTag writeLargeItemStack(ItemStack itemStack) {

    CompoundTag compoundTag = itemStack.serializeNBT();
    compoundTag.putInt("CountLarge", itemStack.getCount());
    return compoundTag;
  }

  private StackHelper() {
    //
  }

}
