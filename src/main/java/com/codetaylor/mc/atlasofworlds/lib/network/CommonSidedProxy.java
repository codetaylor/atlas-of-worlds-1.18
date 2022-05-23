package com.codetaylor.mc.atlasofworlds.lib.network;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.ClientboundPacketBlockEntityData;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraftforge.eventbus.api.IEventBus;

public class CommonSidedProxy
    implements INetworkSidedProxy {

  protected final IPacketService packetService;
  protected final IBlockEntityDataService blockEntityDataService;

  public CommonSidedProxy(IPacketService packetService, IBlockEntityDataService blockEntityDataService) {

    this.packetService = packetService;
    this.blockEntityDataService = blockEntityDataService;
  }

  @Override
  public void initialize() {

    this.packetService.registerMessage(ClientboundPacketBlockEntityData.class, ClientboundPacketBlockEntityData.class);
  }

  @Override
  public void registerModEventHandlers(IEventBus modEventBus) {
    //
  }

  @Override
  public void registerForgeEventHandlers(IEventBus forgeEventBus) {
    //
  }
}
