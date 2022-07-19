package com.codetaylor.mc.atlasofworlds.atlas.client;

import com.codetaylor.mc.atlasofworlds.atlas.client.registry.ClientSetupEventHandler;
import com.codetaylor.mc.atlasofworlds.atlas.common.CommonSidedProxy;
import com.codetaylor.mc.atlasofworlds.lib.dimension.api.IDimensionManager;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientSidedProxy
    extends CommonSidedProxy {

  public ClientSidedProxy(IPacketService packetService, IBlockEntityDataService blockEntityDataService, IDimensionManager dimensionManager) {

    super(packetService, blockEntityDataService, dimensionManager);
  }

  @Override
  public void registerModEventHandlers(IEventBus eventBus) {

    super.registerModEventHandlers(eventBus);
    eventBus.register(new ClientSetupEventHandler());
  }
}
