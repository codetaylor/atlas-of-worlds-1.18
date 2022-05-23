package com.codetaylor.mc.atlasofworlds;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class Resource {

  private static final Map<String, ResourceLocation> CACHE;

  static {
    CACHE = new HashMap<>();
  }

  public static ResourceLocation locate(String path) {

    return CACHE.computeIfAbsent(path, p -> new ResourceLocation(AtlasOfWorldsMod.MOD_ID, p));
  }

  private Resource() {
    //
  }
}
