package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlockEntity;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlockEntity;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public record BlockEntityRegistrationHandler(IBlockEntityDataService blockEntityDataService) {

  @SubscribeEvent
  public void register(RegistryEvent.Register<BlockEntityType<?>> event) {

    IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();

    registry.register(BlockEntityType.Builder.of((blockPos, blockState) -> new MapDeviceBlockEntity(blockPos, blockState, this.blockEntityDataService)).build(null).setRegistryName(MapDeviceBlock.NAME));
    registry.register(BlockEntityType.Builder.of((blockPos, blockState) -> new MapDevicePortalBlockEntity(blockPos, blockState, this.blockEntityDataService)).build(null).setRegistryName(MapDevicePortalBlock.NAME));
  }
}
