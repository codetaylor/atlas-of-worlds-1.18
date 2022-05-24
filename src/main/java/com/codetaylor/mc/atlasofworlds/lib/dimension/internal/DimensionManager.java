/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2022 Joseph Bettendorff a.k.a. "Commoble"
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.codetaylor.mc.atlasofworlds.lib.dimension.internal;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.google.common.collect.Lists;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import com.codetaylor.mc.atlasofworlds.lib.dimension.api.IDimensionManager;
import com.codetaylor.mc.atlasofworlds.lib.dimension.api.UnregisterDimensionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * InfiniverseAPI internal implementation
 */
public final class DimensionManager
    implements IDimensionManager {

  private static final Set<ResourceKey<Level>> VANILLA_LEVELS = Set.of(Level.OVERWORLD, Level.NETHER, Level.END);

  private final IPacketService packetService;

  private Set<ResourceKey<Level>> levelsPendingUnregistration;

  public DimensionManager(IPacketService packetService) {

    this.packetService = packetService;
    this.levelsPendingUnregistration = new HashSet<>();
  }

  // ---------------------------------------------------------------------------
  // Implementation
  // ---------------------------------------------------------------------------

  /**
   * Gets a level, dynamically creating and registering one if it doesn't exist.<br>
   * The dimension registry is stored in the server's level file, all previously registered dimensions are loaded
   * and recreated and re-registered whenever the server starts.<br>
   * This can be used for making dynamic dimensions at runtime; static dimensions should be defined in json instead.<br>
   *
   * @param server           a MinecraftServer instance (you can get this from a ServerPlayerEntity or ServerWorld)
   * @param levelKey         A ResourceKey for your level
   * @param dimensionFactory A function that produces a new LevelStem (dimension) instance if necessary<br>
   *                         If this factory is used, it should be assumed that intended dimension has not been created or registered yet,
   *                         so making the factory attempt to get this dimension from the server's dimension registry will fail
   * @return Returns a ServerLevel, creating and registering a world and dimension for it if the world does not already exist
   */
  @Override
  public ServerLevel getOrCreateLevel(final MinecraftServer server, final ResourceKey<Level> levelKey, final Supplier<LevelStem> dimensionFactory) {

    // this is marked as deprecated, but it's not called from anywhere, and I'm not sure how old it is,
    // it's probably left over from forge's previous dimension api
    // in any case we need to get at the server's world field, and if we didn't use this getter,
    // then we'd just end up making a private-field-getter for it ourselves anyway

    @SuppressWarnings("deprecation")
    Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
    @Nullable ServerLevel existingLevel = map.get(levelKey);

    // if the world already exists, return it
    return (existingLevel == null)
        ? createAndRegisterLevel(server, map, levelKey, dimensionFactory)
        : existingLevel;
  }

  /**
   * Schedules a non-vanilla level/dimension to be unregistered and removed at the end of the current server tick.<br>
   * This will have the following effects:<br>
   * <ul>
   * <li>Unregistered levels will stop ticking.
   * <li>Unregistered dimensions will not be loaded on server startup unless and until they are registered again (via {@link DimensionManager#getOrCreateLevel}.
   * <li>Players still present in the given level will, when the level is removed, be ejected to their spawn points.
   * <li>Players who have respawn points in levels being unloaded will have their spawn points reset to the overworld and respawned there.
   * </ul>
   * Unregistering a level does not delete the region files or other persistant data associated with the level.<br>
   * If a level is reregistered after unregistering it, the level will retain all prior data (unless manually deleted by a server admin.)<br>
   * This has no effect on the vanilla dimensions (The Overworld, The Nether, and The End);
   * this is because vanilla will automatically reconstitute these anyway if we try to remove them,
   * so we disallow their removal to avoid strangeness.<br>
   *
   * @param server        The server to remove the dimension from
   * @param levelToRemove The resource key for the level to be unregistered
   */
  @Override
  public void markDimensionForUnregistration(final MinecraftServer server, final ResourceKey<Level> levelToRemove) {

    if (!VANILLA_LEVELS.contains(levelToRemove)) {
      this.levelsPendingUnregistration.add(levelToRemove);
    }
  }

  /**
   * @return An immutable copy of the dimensions that will be unregistered at the end of the current server tick.
   * (returns an empty set if called while no server is running)
   */
  @Override
  public Set<ResourceKey<Level>> getLevelsPendingUnregistration() {

    return ImmutableSet.copyOf(this.levelsPendingUnregistration);
  }

  // ---------------------------------------------------------------------------
  // Internal
  // ---------------------------------------------------------------------------

  @SuppressWarnings("deprecation") // markWorldsDirty is deprecated, see below
  private ServerLevel createAndRegisterLevel(final MinecraftServer server, final Map<ResourceKey<Level>, ServerLevel> map, final ResourceKey<Level> levelKey, Supplier<LevelStem> dimensionFactory) {
    // get everything we need to create the dimension and the level
    final ServerLevel overworld = server.getLevel(Level.OVERWORLD);

    // dimension keys have a 1:1 relationship with level keys, they have the same IDs as well
    final ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, levelKey.location());
    final LevelStem dimension = dimensionFactory.get();

    // the int in create() here is radius of chunks to watch, 11 is what the server uses when it initializes levels
    final ChunkProgressListener chunkProgressListener = server.progressListenerFactory.create(11);
    final WorldData worldData = server.getWorldData();
    final WorldGenSettings worldGenSettings = worldData.worldGenSettings();
    final DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());

    // now we have everything we need to create the dimension and the level
    // this is the same order server init creates levels:
    // the dimensions are already registered when levels are created, we'll do that first
    // then instantiate level, add border listener, add to map, fire world load event

    // register the actual dimension
    Registry<LevelStem> dimensionRegistry = worldGenSettings.dimensions();

    if (dimensionRegistry instanceof WritableRegistry<LevelStem> writableRegistry) {
      writableRegistry.register(dimensionKey, dimension, Lifecycle.stable());

    } else {
      throw new IllegalStateException(String.format("Unable to register dimension %s -- dimension registry not writable", dimensionKey.location()));
    }

    // create the level instance
    final ServerLevel newLevel = new ServerLevel(
        server,
        server.executor,
        server.storageSource,
        derivedLevelData,
        levelKey,
        dimension.typeHolder(),
        chunkProgressListener,
        dimension.generator(),
        worldGenSettings.isDebug(),
        net.minecraft.world.level.biome.BiomeManager.obfuscateSeed(worldGenSettings.seed()),
        List.of(), // "special spawn list"
        // phantoms, travelling traders, patrolling/sieging raiders, and cats are overworld special spawns
        // this is always empty for non-overworld dimensions (including json dimensions)
        // these spawners are ticked when the world ticks to do their spawning logic,
        // mods that need "special spawns" for their own dimensions should implement them via tick events or other systems
        false // "tick time", true for overworld, always false for nether, end, and json dimensions
    );

    // add world border listener, for parity with json dimensions
    // the vanilla behaviour is that world borders exist in every dimension simultaneously with the same size and position
    // these border listeners are automatically added to the overworld as worlds are loaded, so we should do that here too
    // TODO if world-specific world borders are ever added, change it here too
    overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newLevel.getWorldBorder()));

    // register level
    map.put(levelKey, newLevel);

    // update forge's world cache so the new level can be ticked
    server.markWorldsDirty();

    // fire world load event
    MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newLevel));

    // update clients' dimension lists
    this.packetService.sendToAll(new ClientboundPacketUpdateDimensions(Set.of(levelKey), true));

    return newLevel;
  }

  // ---------------------------------------------------------------------------
  // Events
  // ---------------------------------------------------------------------------

  @SubscribeEvent
  public void on(TickEvent.ServerTickEvent event) {

    if (event.phase == TickEvent.Phase.END) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

      if (server != null) {
        this.unregisterScheduledDimensions(server);
      }
    }
  }

  @SubscribeEvent
  public void on(ServerStoppedEvent event) {

    // clear state on server exit (important for single player worlds)
    this.levelsPendingUnregistration = new HashSet<>();
  }

  private void unregisterScheduledDimensions(final MinecraftServer server) {

    if (this.levelsPendingUnregistration.isEmpty()) {
      return;
    }

    // flush the buffer
    final Set<ResourceKey<Level>> keysToRemove = this.levelsPendingUnregistration;
    this.levelsPendingUnregistration = new HashSet<>();

    // we need to remove the dimension/level from three places:
    // the server's dimension/levelstem registry, the server's level registry, and
    // the overworld's border listener
    // the level registry is just a simple map and the border listener has a remove() method
    // the dimension registry has five sub-collections that need to be cleaned up
    // we should also eject players from removed worlds so they don't get stuck there

    final WorldGenSettings worldGenSettings = server.getWorldData().worldGenSettings();
    final Set<ResourceKey<Level>> removedLevelKeys = new HashSet<>();
    final ServerLevel overworld = server.getLevel(Level.OVERWORLD);

    for (final ResourceKey<Level> levelKeyToRemove : keysToRemove) {
      final @Nullable ServerLevel levelToRemove = server.getLevel(levelKeyToRemove);

      if (levelToRemove == null) {
        continue;
      }

      UnregisterDimensionEvent unregisterDimensionEvent = new UnregisterDimensionEvent(levelToRemove);
      MinecraftForge.EVENT_BUS.post(unregisterDimensionEvent);

      if (unregisterDimensionEvent.isCanceled()) {
        continue;
      }

      // null if specified level not present
      final @Nullable ServerLevel removedLevel = server.forgeGetWorldMap().remove(levelKeyToRemove);

      if (removedLevel != null) // if we removed the key from the map
      {
        // eject players from dead world
        // iterate over a copy as the world will remove players from the original list
        for (final ServerPlayer player : Lists.newArrayList(removedLevel.players())) {
          // send players to their respawn point
          ResourceKey<Level> respawnKey = player.getRespawnDimension();
          // if we're removing their respawn world then just send them to the overworld
          if (keysToRemove.contains(respawnKey)) {
            respawnKey = Level.OVERWORLD;
            player.setRespawnPosition(respawnKey, null, 0, false, false);
          }
          if (respawnKey == null) {
            respawnKey = Level.OVERWORLD;
          }
          @Nullable ServerLevel destinationLevel = server.getLevel(respawnKey);
          if (destinationLevel == null) {
            destinationLevel = overworld;
          }

          @Nullable
          BlockPos destinationPos = player.getRespawnPosition();
          if (destinationPos == null) {
            destinationPos = destinationLevel.getSharedSpawnPos();
          }

          final float respawnAngle = player.getRespawnAngle();
          // "respawning" the player via the player list schedules a task in the server to
          // run after the post-server tick
          // that causes some minor logspam due to the player's world no longer being
          // loaded
          // teleporting the player via a teleport avoids this
          player.teleportTo(destinationLevel, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), respawnAngle, 0F);
        }

        // save the world now or it won't be saved later and data that may be wanted to
        // be kept may be lost
        removedLevel.save(null, false, removedLevel.noSave());

        // fire world unload event -- when the server stops, this would fire after
        // worlds get saved, we'll do that here too
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(removedLevel));

        // remove the world border listener if possible
        final WorldBorder overworldBorder = overworld.getWorldBorder();
        final WorldBorder removedWorldBorder = removedLevel.getWorldBorder();
        BorderChangeListener targetListener = null;

        for (BorderChangeListener listener : overworldBorder.listeners) {

          if (listener instanceof BorderChangeListener.DelegateBorderChangeListener delegate
              && removedWorldBorder == delegate.worldBorder) {
            targetListener = listener;
            break;
          }
        }

        if (targetListener != null) {
          overworldBorder.removeListener(targetListener);
        }

        // track the removed level
        removedLevelKeys.add(levelKeyToRemove);
      }
    }

    if (!removedLevelKeys.isEmpty()) {
      // replace the old dimension registry with a new one containing the dimensions
      // that weren't removed, in the same order
      final Registry<LevelStem> oldRegistry = worldGenSettings.dimensions();
      final MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, oldRegistry.lifecycle(), null);

      for (final var entry : oldRegistry.entrySet()) {
        final ResourceKey<LevelStem> oldKey = entry.getKey();
        final ResourceKey<Level> oldLevelKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, oldKey.location());
        final LevelStem dimension = entry.getValue();

        if (oldKey != null && dimension != null && !removedLevelKeys.contains(oldLevelKey)) {
          newRegistry.register(oldKey, dimension, oldRegistry.lifecycle(dimension));
        }
      }

      // then replace the old registry with the new registry
      worldGenSettings.dimensions = newRegistry;

      // update the server's levels so dead levels don't get ticked
      server.markWorldsDirty();

      // notify client of the removed levels
      this.packetService.sendToAll(new ClientboundPacketUpdateDimensions(removedLevelKeys, false));
    }
  }
}
