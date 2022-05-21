package com.codetaylor.mc.atlasofworlds.lib.network;

public interface IClientConfig {

  boolean isServiceMonitorEnabled();

  int getServiceMonitorUpdateIntervalTicks();

  int getServiceMonitorTrackedIndex();
}
