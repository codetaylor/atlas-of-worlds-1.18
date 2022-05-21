package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.AtlasOfWorldsMod;
import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public record ItemRegistrationEventHandler() {

  @SubscribeEvent
  public void register(RegistryEvent.Register<Item> event) {

    CreativeModeTab tab = new CreativeModeTab(AtlasOfWorldsMod.MOD_ID) {

      @Override
      public ItemStack makeIcon() {

        return new ItemStack(AtlasModule.Blocks.MAP_DEVICE);
      }
    };

    IForgeRegistry<Item> registry = event.getRegistry();

    //noinspection ConstantConditions
    registry.register(new BlockItem(AtlasModule.Blocks.MAP_DEVICE, new Item.Properties().tab(tab)).setRegistryName(MapDeviceBlock.NAME));
  }
}
