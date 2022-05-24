package com.codetaylor.mc.atlasofworlds.lib.dimension.api;

import com.codetaylor.mc.atlasofworlds.lib.dimension.internal.ClientboundPacketUpdateDimensions;
import com.codetaylor.mc.atlasofworlds.lib.dimension.internal.DimensionManager;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import net.minecraftforge.eventbus.api.IEventBus;

public final class DimensionAPI {

  public static void initialize(IEventBus forgeEventBus, IPacketService packetService, DimensionManager dimensionManager) {

    packetService.registerMessage(ClientboundPacketUpdateDimensions.class, ClientboundPacketUpdateDimensions.class);
    forgeEventBus.register(dimensionManager);
  }

  public static DimensionManager createDimensionManager(IPacketService packetService) {

    return new DimensionManager(packetService);
  }

  private DimensionAPI() {
    //
  }
}
