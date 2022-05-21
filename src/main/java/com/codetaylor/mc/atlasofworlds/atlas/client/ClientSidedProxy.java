package com.codetaylor.mc.atlasofworlds.atlas.client;

import com.codetaylor.mc.atlasofworlds.atlas.common.CommonSidedProxy;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;

public class ClientSidedProxy
    extends CommonSidedProxy {

  public ClientSidedProxy(IPacketService packetService, IBlockEntityDataService blockEntityDataService) {

    super(packetService, blockEntityDataService);
  }
}
