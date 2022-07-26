package com.codetaylor.mc.atlasofworlds.lib.network;

import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client.BlockEntityDataServiceClientMonitors;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client.BlockEntityDataServiceOverlayRenderer;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientSidedProxy
    extends CommonSidedProxy {

  private BlockEntityDataServiceOverlayRenderer blockEntityDataServiceOverlayRenderer;

  public ClientSidedProxy(IPacketService packetService, IBlockEntityDataService blockEntityDataService) {

    super(packetService, blockEntityDataService);
  }

  @Override
  public void initialize() {

    super.initialize();

    IClientConfig clientConfig = new IClientConfig() {

      @Override
      public boolean isServiceMonitorEnabled() {

        return true;
      }

      @Override
      public int getServiceMonitorUpdateIntervalTicks() {

        return 20;
      }

      @Override
      public int getServiceMonitorTrackedIndex() {

        return 10;
      }
    };

    BlockEntityDataServiceClientMonitors.initialize(clientConfig);

    this.blockEntityDataServiceOverlayRenderer = new BlockEntityDataServiceOverlayRenderer(clientConfig);
  }

  @Override
  public void registerForgeEventHandlers(IEventBus forgeEventBus) {

    super.registerForgeEventHandlers(forgeEventBus);
    forgeEventBus.addListener(this.blockEntityDataServiceOverlayRenderer::onRenderGameOverlayPostEvent);
  }
}
