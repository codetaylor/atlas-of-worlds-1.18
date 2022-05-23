package com.codetaylor.mc.atlasofworlds.atlas.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class AtlasMapItem
    extends Item {

  public static final String NAME_COMMON_0 = "map_common_0";

  public AtlasMapItem(CreativeModeTab tab) {

    super(new Item.Properties().stacksTo(1).tab(tab));
  }

  @Override
  public boolean isEnchantable(@Nonnull ItemStack itemStack) {

    return false;
  }
}
