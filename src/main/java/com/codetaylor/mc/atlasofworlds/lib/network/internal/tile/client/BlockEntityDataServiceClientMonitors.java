package com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client;

import com.codetaylor.mc.atlasofworlds.lib.network.IClientConfig;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataTracker;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataServiceLogger;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.IBlockEntityData;
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
public class BlockEntityDataServiceClientMonitors {

  private static BlockEntityDataServiceClientMonitors instance;

  private static final short TOTAL_INTERVAL_COUNT = 2 * 60;
  private static final int CACHE_CLEANUP_INTERVAL_TICKS = 10 * 20;

  private final BlockEntityDataTrackerUpdateMonitor trackerUpdateMonitor;

  /**
   * Monitors all network traffic for all blockEntity data services.
   */
  public final BlockEntityDataServiceClientMonitor totalServiceClientMonitor;

  /**
   * Monitors network traffic, indexed by world position.
   */
  private final LoadingCache<BlockPos, BlockEntityDataServiceClientMonitor> loadingCacheBlockPosTotal;

  private final IClientConfig clientConfig;

  private int cacheCleanupCounter;

  public static void initialize(IClientConfig clientConfig) {

    BlockEntityDataServiceClientMonitors.instance = new BlockEntityDataServiceClientMonitors(
        clientConfig,
        new BlockEntityDataTrackerUpdateMonitor(),
        new BlockEntityDataServiceClientMonitor(
            clientConfig::getServiceMonitorUpdateIntervalTicks,
            TOTAL_INTERVAL_COUNT
        ),
        CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {

              public @NotNull BlockEntityDataServiceClientMonitor load(@Nonnull BlockPos pos) {

                return new BlockEntityDataServiceClientMonitor(clientConfig::getServiceMonitorUpdateIntervalTicks, TOTAL_INTERVAL_COUNT);
              }
            })
    );
  }

  public static BlockEntityDataServiceClientMonitors getInstance() {

    return BlockEntityDataServiceClientMonitors.instance;
  }

  private BlockEntityDataServiceClientMonitors(
      IClientConfig clientConfig,
      BlockEntityDataTrackerUpdateMonitor trackerUpdateMonitor,
      BlockEntityDataServiceClientMonitor totalServiceClientMonitor,
      LoadingCache<BlockPos, BlockEntityDataServiceClientMonitor> loadingCacheBlockPosTotal
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

        for (BlockEntityDataServiceClientMonitor value : this.loadingCacheBlockPosTotal.asMap().values()) {
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
   * Called when a packet from the block entity data service is received on
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

      BlockEntityDataServiceClientMonitor monitor = null;

      try {
        monitor = this.loadingCacheBlockPosTotal.get(pos);

      } catch (ExecutionException e) {
        BlockEntityDataServiceLogger.LOGGER.error("", e);
      }

      if (monitor != null) {
        monitor.receiveBytes(size);
      }
    }
  }

  public void onClientTrackerUpdateReceived(BlockPos pos, Class<? extends IBlockEntityData> tileDataClass) {

    if (this.clientConfig.isServiceMonitorEnabled()) {
      this.trackerUpdateMonitor.onClientTrackerUpdateReceived(pos, tileDataClass);
    }
  }

  // ---------------------------------------------------------------------------
  // - Static Accessors
  // ---------------------------------------------------------------------------

  @Nullable
  public BlockEntityDataServiceClientMonitor findMonitorForPosition(BlockPos pos) {

    if (this.loadingCacheBlockPosTotal.asMap().containsKey(pos)) {

      try {
        return this.loadingCacheBlockPosTotal.get(pos);

      } catch (ExecutionException e) {
        BlockEntityDataServiceLogger.LOGGER.error("", e);
      }
    }

    return null;
  }

  public BlockEntityDataTrackerUpdateMonitor getTrackerUpdateMonitor() {

    return this.trackerUpdateMonitor;
  }

}
