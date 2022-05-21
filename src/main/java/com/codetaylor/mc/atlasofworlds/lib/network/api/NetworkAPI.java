package com.codetaylor.mc.atlasofworlds.lib.network.api;

import com.codetaylor.mc.atlasofworlds.lib.network.internal.packet.PacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataServiceContainer;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.resources.ResourceLocation;

public final class NetworkAPI {

  public static IPacketService createPacketService(String modId, String channelName, String protocolVersion) {

    return PacketService.create(modId, channelName, protocolVersion);
  }

  public static IBlockEntityDataService createTileDataService(String modId, String serviceName, IPacketService packetService) {

    return BlockEntityDataServiceContainer.INSTANCE.get().register(new ResourceLocation(modId, serviceName), packetService);
  }
}
