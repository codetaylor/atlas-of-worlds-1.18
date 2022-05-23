package com.codetaylor.mc.atlasofworlds.atlas.common.container;

import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class MapDeviceContainer
    extends BaseContainer {

  public static final String NAME = "mapdevice";

  private final BlockEntity blockEntity;
  private final Player player;
  private final InvWrapper playerInventory;

  public MapDeviceContainer(int containerId, BlockPos blockPos, Inventory playerInventory, Player player) {

    super(AtlasModule.MenuTypes.MAP_DEVICE, containerId, playerInventory);
    this.blockEntity = player.getCommandSenderWorld().getBlockEntity(blockPos);
    this.player = player;
    this.playerInventory = new InvWrapper(playerInventory);

    this.containerPlayerInventoryAdd();
    this.containerPlayerHotbarAdd();
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
