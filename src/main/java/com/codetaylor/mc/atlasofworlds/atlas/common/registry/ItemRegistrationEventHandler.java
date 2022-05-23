package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.AtlasOfWorldsMod;
import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.item.AtlasMapItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

public record ItemRegistrationEventHandler() {

  @SuppressWarnings("ConstantConditions")
  @SubscribeEvent
  public void register(RegistryEvent.Register<Item> event) {

    CreativeModeTab tab = new CreativeModeTab(AtlasOfWorldsMod.MOD_ID) {

      @Nonnull
      @Override
      public ItemStack makeIcon() {

        return new ItemStack(AtlasModule.Blocks.MAP_DEVICE);
      }
    };

    IForgeRegistry<Item> registry = event.getRegistry();

    // ---------------------------------------------------------------------------
    // Block Items
    // ---------------------------------------------------------------------------

    registry.register(new BlockItem(AtlasModule.Blocks.MAP_DEVICE, new Item.Properties().tab(tab)).setRegistryName(MapDeviceBlock.NAME));
    registry.register(new BlockItem(AtlasModule.Blocks.MAP_DEVICE_PORTAL, new Item.Properties().tab(tab)).setRegistryName(MapDevicePortalBlock.NAME));

    // ---------------------------------------------------------------------------
    // Maps
    // ---------------------------------------------------------------------------

    registry.register(new AtlasMapItem(tab).setRegistryName(AtlasMapItem.NAME_COMMON_0));
  }
}
