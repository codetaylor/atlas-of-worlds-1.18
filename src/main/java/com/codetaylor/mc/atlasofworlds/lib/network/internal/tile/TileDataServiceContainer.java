package com.codetaylor.mc.atlasofworlds.lib.network.internal.tile;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.ITileDataService;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class retains collections of registered tile data services.
 * <p>
 * Must be registered with the Forge event bus on both the client and server.
 */
public final class TileDataServiceContainer {

  public static final Lazy<TileDataServiceContainer> INSTANCE = Lazy.of(TileDataServiceContainer::new);

  private final Map<ResourceLocation, ITileDataService> serviceMap;
  private final Int2ObjectOpenHashMap<ITileDataService> serviceIdMap;

  private int nextId;

  private TileDataServiceContainer() {

    MinecraftForge.EVENT_BUS.addListener(this::onServerTickEvent);

    this.serviceMap = new HashMap<>();
    this.serviceIdMap = new Int2ObjectOpenHashMap<>();
  }

  public ITileDataService register(ResourceLocation location, IPacketService packetService) {

    if (this.serviceMap.get(location) != null) {
      throw new IllegalStateException("Tile data service already registered for id: " + location);
    }

    TileDataService service = new TileDataService(this.nextId, packetService);
    this.serviceMap.put(location, service);
    this.serviceIdMap.put(this.nextId, service);

    this.nextId += 1;

    return service;
  }

  @Nullable
  public ITileDataService find(ResourceLocation location) {

    return this.serviceMap.get(location);
  }

  @Nullable
  public ITileDataService find(int serviceId) {

    return this.serviceIdMap.get(serviceId);
  }

  private void onServerTickEvent(TickEvent.ServerTickEvent event) {

    if (event.phase == TickEvent.Phase.END) {

      for (ITileDataService service : this.serviceMap.values()) {
        service.update();
      }
    }
  }
}