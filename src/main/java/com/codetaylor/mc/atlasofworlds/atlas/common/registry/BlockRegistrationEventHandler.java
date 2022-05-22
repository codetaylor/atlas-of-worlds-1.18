package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlock;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public record BlockRegistrationEventHandler(IBlockEntityDataService blockEntityDataService) {

  @SubscribeEvent
  public void register(RegistryEvent.Register<Block> event) {

    IForgeRegistry<Block> registry = event.getRegistry();

    registry.register(new MapDeviceBlock(this.blockEntityDataService).setRegistryName(MapDeviceBlock.NAME));
    registry.register(new MapDevicePortalBlock(this.blockEntityDataService).setRegistryName(MapDevicePortalBlock.NAME));
  }
}
