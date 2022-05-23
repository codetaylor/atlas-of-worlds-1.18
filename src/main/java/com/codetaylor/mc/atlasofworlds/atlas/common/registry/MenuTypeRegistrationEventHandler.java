package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.atlas.common.container.MapDeviceContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public record MenuTypeRegistrationEventHandler() {

  @SubscribeEvent
  public void register(RegistryEvent.Register<MenuType<?>> event) {

    IForgeRegistry<MenuType<?>> registry = event.getRegistry();

    registry.register(IForgeMenuType.create((windowId, inv, data) -> new MapDeviceContainer(windowId, data.readBlockPos(), inv, inv.player)).setRegistryName(MapDeviceContainer.NAME));
  }
}
