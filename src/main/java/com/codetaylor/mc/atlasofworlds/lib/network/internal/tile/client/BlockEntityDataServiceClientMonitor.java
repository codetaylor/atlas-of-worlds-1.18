package com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockEntityDataServiceClientMonitor {

  // ---------------------------------------------------------------------------
  // - Monitor Instance
  // ---------------------------------------------------------------------------

  private final IntArrayList totalBytesReceivedPerSecond;
  private final UpdateIntervalProvider updateIntervalTicks;
  private final int totalIntervalCount;

  private int totalBytesReceived;
  private short tickCounter;

  public BlockEntityDataServiceClientMonitor(UpdateIntervalProvider updateIntervalTicks, int totalIntervalCount) {

    totalBytesReceivedPerSecond = new IntArrayList(totalIntervalCount);
    this.updateIntervalTicks = updateIntervalTicks;
    this.totalIntervalCount = totalIntervalCount;
  }

  /**
   * Call once per tick to update the monitor.
   */
  public void update() {

    this.tickCounter += 1;

    if (this.tickCounter >= this.updateIntervalTicks.getUpdateInterval()) {
      this.tickCounter = 0;
      this.totalBytesReceivedPerSecond.add(0, this.totalBytesReceived);
      this.totalBytesReceived = 0;

      if (this.totalBytesReceivedPerSecond.size() > this.totalIntervalCount) {
        this.totalBytesReceivedPerSecond.removeInt(this.totalBytesReceivedPerSecond.size() - 1);
      }
    }
  }

  protected void receiveBytes(int size) {

    this.totalBytesReceived += size;
  }

  public int size() {

    return this.totalBytesReceivedPerSecond.size();
  }

  public int get(int index) {

    return this.totalBytesReceivedPerSecond.getInt(index);
  }

  public int getTotalIntervalCount() {

    return this.totalIntervalCount;
  }

  interface UpdateIntervalProvider {

    int getUpdateInterval();
  }

}
