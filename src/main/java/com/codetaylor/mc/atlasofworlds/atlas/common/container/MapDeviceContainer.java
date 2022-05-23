package com.codetaylor.mc.atlasofworlds.atlas.common.container;

import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlockEntity;
import com.codetaylor.mc.atlasofworlds.atlas.common.item.AtlasMapItem;
import com.codetaylor.mc.atlasofworlds.atlas.common.item.AtlasMapStone;
import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class MapDeviceContainer
    extends BaseContainer {

  public static final String NAME = "mapdevice";

  private final MapDeviceBlockEntity blockEntity;
  private final Player player;
  private final InvWrapper playerInventory;

  private final int slotIndexMapMatrixStart, slotIndexMapMatrixEnd;
  private final int slotIndexStoneStart, slotIndexStoneEnd;

  public MapDeviceContainer(int containerId, BlockPos blockPos, Inventory playerInventory, Player player) {

    super(AtlasModule.MenuTypes.MAP_DEVICE, containerId, playerInventory);
    this.blockEntity = (MapDeviceBlockEntity) player.getCommandSenderWorld().getBlockEntity(blockPos);
    this.player = player;
    this.playerInventory = new InvWrapper(playerInventory);

    {
      this.slotIndexMapMatrixStart = this.nextSlotIndex;
      MapDeviceBlockEntity.MapMatrixStackHandler stackHandler = this.blockEntity.getMapMatrixStackHandler();

      for (int y = 0; y < stackHandler.getHeight(); y++) {
        for (int x = 0; x < stackHandler.getWidth(); x++) {
          this.containerSlotAdd(new SlotItemHandler(stackHandler, x + y * stackHandler.getWidth(), 71 + x * 18, 67 + y * 18));
        }
      }
      this.slotIndexMapMatrixEnd = (this.nextSlotIndex - 1);
    }

    {
      this.slotIndexStoneStart = this.nextSlotIndex;
      MapDeviceBlockEntity.StoneStackHandler stackHandler = this.blockEntity.getStoneStackHandler();

      for (int i = 0; i < stackHandler.getSlots(); i++) {
        this.containerSlotAdd(new SlotItemHandler(stackHandler, i, 30 + i * 20, 44));
      }
      this.slotIndexStoneEnd = (this.nextSlotIndex - 1);
    }

    this.containerPlayerInventoryAdd();
    this.containerPlayerHotbarAdd();
  }

  @Nullable
  @Override
  protected Slot containerItemStackSlotTargetGet(ItemStack itemStack) {

    Item item = itemStack.getItem();

    if (item instanceof AtlasMapItem) {

      for (int i = 0; i <= this.slotIndexMapMatrixEnd - this.slotIndexMapMatrixStart; i++) {
        Slot slot = this.slots.get(i + this.slotIndexMapMatrixStart);

        if (!slot.hasItem()) {
          return slot;
        }
      }

    } else if (item instanceof AtlasMapStone) {

      for (int i = 0; i <= this.slotIndexStoneEnd - this.slotIndexStoneStart; i++) {
        Slot slot = this.slots.get(i + this.slotIndexStoneStart);

        if (!slot.hasItem()) {
          return slot;
        }
      }
    }

    return null;
  }

  @Override
  public boolean stillValid(@Nonnull Player player) {

    return stillValid(ContainerLevelAccess.create(this.blockEntity.getLevel(), blockEntity.getBlockPos()), this.player, AtlasModule.Blocks.MAP_DEVICE);
  }

  @Override
  protected int containerInventoryPositionGetY() {

    return DEFAULT_INVENTORY_POSITION_Y + 55;
  }

  @Override
  protected int containerHotbarPositionGetY() {

    return DEFAULT_HOTBAR_POSITION_Y + 55;
  }
}
