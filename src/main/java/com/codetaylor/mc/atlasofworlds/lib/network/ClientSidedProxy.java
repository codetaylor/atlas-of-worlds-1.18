package com.codetaylor.mc.atlasofworlds.lib.network;

import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client.TileDataServiceClientMonitors;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client.TileDataServiceOverlayRenderer;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientSidedProxy
    extends CommonSidedProxy {

  private TileDataServiceOverlayRenderer tileDataServiceOverlayRenderer;

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

    TileDataServiceClientMonitors.initialize(clientConfig);

    this.tileDataServiceOverlayRenderer = new TileDataServiceOverlayRenderer(clientConfig);
  }

  @Override
  public void registerForgeEventHandlers(IEventBus forgeEventBus) {

    super.registerForgeEventHandlers(forgeEventBus);
    forgeEventBus.addListener(this.tileDataServiceOverlayRenderer::onRenderGameOverlayPostEvent);
  }
}
