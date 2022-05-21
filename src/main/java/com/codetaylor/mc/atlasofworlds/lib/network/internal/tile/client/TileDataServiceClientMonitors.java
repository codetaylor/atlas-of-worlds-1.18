package com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client;

import com.codetaylor.mc.atlasofworlds.lib.network.IClientConfig;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataTracker;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.TileDataServiceLogger;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.ITileData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
public class TileDataServiceClientMonitors {

  private static TileDataServiceClientMonitors instance;

  private static final short TOTAL_INTERVAL_COUNT = 2 * 60;
  private static final int CACHE_CLEANUP_INTERVAL_TICKS = 10 * 20;

  private final TileDataTrackerUpdateMonitor trackerUpdateMonitor;

  /**
   * Monitors all network traffic for all tile data services.
   */
  public final TileDataServiceClientMonitor totalServiceClientMonitor;

  /**
   * Monitors network traffic, indexed by world position.
   */
  private final LoadingCache<BlockPos, TileDataServiceClientMonitor> loadingCacheBlockPosTotal;

  private final IClientConfig clientConfig;

  private int cacheCleanupCounter;

  public static void initialize(IClientConfig clientConfig) {

    TileDataServiceClientMonitors.instance = new TileDataServiceClientMonitors(
        clientConfig,
        new TileDataTrackerUpdateMonitor(),
        new TileDataServiceClientMonitor(
            clientConfig::getServiceMonitorUpdateIntervalTicks,
            TOTAL_INTERVAL_COUNT
        ),
        CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {

              public @NotNull TileDataServiceClientMonitor load(@Nonnull BlockPos pos) {

                return new TileDataServiceClientMonitor(clientConfig::getServiceMonitorUpdateIntervalTicks, TOTAL_INTERVAL_COUNT);
              }
            })
    );
  }

  public static TileDataServiceClientMonitors getInstance() {

    return TileDataServiceClientMonitors.instance;
  }

  private TileDataServiceClientMonitors(
      IClientConfig clientConfig,
      TileDataTrackerUpdateMonitor trackerUpdateMonitor,
      TileDataServiceClientMonitor totalServiceClientMonitor,
      LoadingCache<BlockPos, TileDataServiceClientMonitor> loadingCacheBlockPosTotal
  ) {

    this.clientConfig = clientConfig;

    MinecraftForge.EVENT_BUS.addListener(this::onClientTickEvent);

    this.trackerUpdateMonitor = trackerUpdateMonitor;
    this.totalServiceClientMonitor = totalServiceClientMonitor;
    this.loadingCacheBlockPosTotal = loadingCacheBlockPosTotal;
  }

  // ---------------------------------------------------------------------------
  // - Events
  // ---------------------------------------------------------------------------

  public void onClientTickEvent(TickEvent.ClientTickEvent event) {

    // Update all the monitors when the client ticks, limit phase so it only
    // updates once.

    if (event.phase == TickEvent.Phase.START) {

      if (!Minecraft.getInstance().isPaused()
          && this.clientConfig.isServiceMonitorEnabled()) {

        this.totalServiceClientMonitor.update();

        for (TileDataServiceClientMonitor value : this.loadingCacheBlockPosTotal.asMap().values()) {
          value.update();
        }

        this.trackerUpdateMonitor.update();
      }

      this.cacheCleanupCounter += 1;

      if (this.cacheCleanupCounter >= CACHE_CLEANUP_INTERVAL_TICKS) {
        this.cacheCleanupCounter = 0;
        this.loadingCacheBlockPosTotal.cleanUp();
      }
    }
  }

  /**
   * Called when a packet from the tile entity data service is received on
   * the client.
   *
   * @param tracker the tracker that received the packet
   * @param pos     the pos of the TE
   * @param size    the size of the packet's TE update buffer in bytes
   */
  public void onClientPacketReceived(BlockEntityDataTracker tracker, BlockPos pos, int size) {

    if (this.clientConfig.isServiceMonitorEnabled()) {

      // --- Total ---

      this.totalServiceClientMonitor.receiveBytes(size);

      // --- Per Position ---

      TileDataServiceClientMonitor monitor = null;

      try {
        monitor = this.loadingCacheBlockPosTotal.get(pos);

      } catch (ExecutionException e) {
        TileDataServiceLogger.LOGGER.error("", e);
      }

      if (monitor != null) {
        monitor.receiveBytes(size);
      }
    }
  }

  public void onClientTrackerUpdateReceived(BlockPos pos, Class<? extends ITileData> tileDataClass) {

    if (this.clientConfig.isServiceMonitorEnabled()) {
      this.trackerUpdateMonitor.onClientTrackerUpdateReceived(pos, tileDataClass);
    }
  }

  // ---------------------------------------------------------------------------
  // - Static Accessors
  // ---------------------------------------------------------------------------

  @Nullable
  public TileDataServiceClientMonitor findMonitorForPosition(BlockPos pos) {

    if (this.loadingCacheBlockPosTotal.asMap().containsKey(pos)) {

      try {
        return this.loadingCacheBlockPosTotal.get(pos);

      } catch (ExecutionException e) {
        TileDataServiceLogger.LOGGER.error("", e);
      }
    }

    return null;
  }

  public TileDataTrackerUpdateMonitor getTrackerUpdateMonitor() {

    return this.trackerUpdateMonitor;
  }

}
