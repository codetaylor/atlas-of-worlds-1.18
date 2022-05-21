package com.codetaylor.mc.atlasofworlds.lib.network.api;

import com.codetaylor.mc.atlasofworlds.lib.network.ClientSidedProxy;
import com.codetaylor.mc.atlasofworlds.lib.network.CommonSidedProxy;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.packet.PacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataServiceContainer;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;

public final class NetworkAPI {

  public static void initialize(IEventBus modEventBus, IEventBus forgeEventBus) {

    CommonSidedProxy proxy = DistExecutor.unsafeRunForDist(() -> ClientSidedProxy::new, () -> CommonSidedProxy::new);
    proxy.initialize();
    proxy.registerModEventHandlers(modEventBus);
    proxy.registerForgeEventHandlers(forgeEventBus);
  }

  public static IPacketService createPacketService(String modId, String channelName, String protocolVersion) {

    return PacketService.create(modId, channelName, protocolVersion);
  }

  public static IBlockEntityDataService createTileDataService(String modId, String serviceName, IPacketService packetService) {

    return BlockEntityDataServiceContainer.INSTANCE.get().register(new ResourceLocation(modId, serviceName), packetService);
  }
}
