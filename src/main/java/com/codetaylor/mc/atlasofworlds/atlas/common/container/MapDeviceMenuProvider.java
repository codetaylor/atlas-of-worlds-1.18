package com.codetaylor.mc.atlasofworlds.atlas.common.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MapDeviceMenuProvider
    implements MenuProvider {

  private static final TranslatableComponent TITLE_COMPONENT = new TranslatableComponent("gui.atlasofworlds.mapdevice.title");

  private final BlockPos blockPos;

  public MapDeviceMenuProvider(BlockPos blockPos) {

    this.blockPos = blockPos;
  }

  @Nonnull
  @Override
  public Component getDisplayName() {

    return TITLE_COMPONENT;
  }

  @ParametersAreNonnullByDefault
  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {

    return new MapDeviceContainer(id, this.blockPos, playerInventory, player);
  }
}
