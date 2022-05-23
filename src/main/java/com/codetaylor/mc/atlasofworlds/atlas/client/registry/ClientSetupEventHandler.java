package com.codetaylor.mc.atlasofworlds.atlas.client.registry;

import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.atlas.client.screen.MapDeviceScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetupEventHandler {

  @SubscribeEvent
  public void on(FMLClientSetupEvent event) {

    event.enqueueWork(() -> {
      MenuScreens.register(AtlasModule.MenuTypes.MAP_DEVICE, MapDeviceScreen::new);
    });
  }
}
