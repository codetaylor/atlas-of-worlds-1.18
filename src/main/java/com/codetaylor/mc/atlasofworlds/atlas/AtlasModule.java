package com.codetaylor.mc.atlasofworlds.atlas;

import com.codetaylor.mc.atlasofworlds.AtlasOfWorldsMod;
import com.codetaylor.mc.atlasofworlds.atlas.client.ClientSidedProxy;
import com.codetaylor.mc.atlasofworlds.atlas.common.CommonSidedProxy;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlockEntity;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlockEntity;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ObjectHolder;

public class AtlasModule {

  public AtlasModule(IEventBus modEventBus, IEventBus forgeEventBus, IPacketService packetService, IBlockEntityDataService blockEntityDataService) {

    IAtlasModuleSidedProxy proxy = DistExecutor.unsafeRunForDist(
        () -> () -> new ClientSidedProxy(packetService, blockEntityDataService),
        () -> () -> new CommonSidedProxy(packetService, blockEntityDataService)
    );
    proxy.initialize();
    proxy.registerModEventHandlers(modEventBus);
    proxy.registerForgeEventHandlers(forgeEventBus);
  }

  @ObjectHolder(AtlasOfWorldsMod.MOD_ID)
  public static class Blocks {

    @ObjectHolder(MapDeviceBlock.NAME)
    public static final MapDeviceBlock MAP_DEVICE;

    @ObjectHolder(MapDevicePortalBlock.NAME)
    public static final MapDevicePortalBlock MAP_DEVICE_PORTAL;

    static {
      MAP_DEVICE = null;
      MAP_DEVICE_PORTAL = null;
    }
  }

  @ObjectHolder(AtlasOfWorldsMod.MOD_ID)
  public static class BlockEntityTypes {

    @ObjectHolder(MapDeviceBlockEntity.NAME)
    public static final BlockEntityType<MapDeviceBlockEntity> MAP_DEVICE;

    @ObjectHolder(MapDevicePortalBlockEntity.NAME)
    public static final BlockEntityType<MapDevicePortalBlockEntity> MAP_DEVICE_PORTAL;

    static {
      MAP_DEVICE = null;
      MAP_DEVICE_PORTAL = null;
    }
  }
}
