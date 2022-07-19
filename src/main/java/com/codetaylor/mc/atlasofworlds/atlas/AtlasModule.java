package com.codetaylor.mc.atlasofworlds.atlas;

import com.codetaylor.mc.atlasofworlds.AtlasOfWorldsMod;
import com.codetaylor.mc.atlasofworlds.Resource;
import com.codetaylor.mc.atlasofworlds.atlas.client.ClientSidedProxy;
import com.codetaylor.mc.atlasofworlds.atlas.common.CommonSidedProxy;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDeviceBlockEntity;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlock;
import com.codetaylor.mc.atlasofworlds.atlas.common.block.MapDevicePortalBlockEntity;
import com.codetaylor.mc.atlasofworlds.atlas.common.container.MapDeviceContainer;
import com.codetaylor.mc.atlasofworlds.atlas.common.level.structure.TestStructure;
import com.codetaylor.mc.atlasofworlds.lib.dimension.api.IDimensionManager;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ObjectHolder;

public class AtlasModule {

  public AtlasModule(IEventBus modEventBus, IEventBus forgeEventBus, IPacketService packetService, IBlockEntityDataService blockEntityDataService, IDimensionManager dimensionManager) {

    IAtlasModuleSidedProxy proxy = DistExecutor.unsafeRunForDist(
        () -> () -> new ClientSidedProxy(packetService, blockEntityDataService, dimensionManager),
        () -> () -> new CommonSidedProxy(packetService, blockEntityDataService, dimensionManager)
    );
    proxy.initialize();
    proxy.registerModEventHandlers(modEventBus);
    proxy.registerForgeEventHandlers(forgeEventBus);
  }

  @ObjectHolder(AtlasOfWorldsMod.MOD_ID)
  public static class Blocks {

    @ObjectHolder(MapDeviceBlock.NAME)
    public static final MapDeviceBlock MAP_DEVICE;

    @ObjectHolder(MapDevicePortalBlock.NAME)
    public static final MapDevicePortalBlock MAP_DEVICE_PORTAL;

    static {
      MAP_DEVICE = null;
      MAP_DEVICE_PORTAL = null;
    }
  }

  @ObjectHolder(AtlasOfWorldsMod.MOD_ID)
  public static class BlockEntityTypes {

    @ObjectHolder(MapDeviceBlock.NAME)
    public static final BlockEntityType<MapDeviceBlockEntity> MAP_DEVICE;

    @ObjectHolder(MapDevicePortalBlock.NAME)
    public static final BlockEntityType<MapDevicePortalBlockEntity> MAP_DEVICE_PORTAL;

    static {
      MAP_DEVICE = null;
      MAP_DEVICE_PORTAL = null;
    }
  }

  @ObjectHolder(AtlasOfWorldsMod.MOD_ID)
  public static class MenuTypes {

    @ObjectHolder(MapDeviceContainer.NAME)
    public static final MenuType<MapDeviceContainer> MAP_DEVICE;

    static {
      MAP_DEVICE = null;
    }
  }

  @ObjectHolder(AtlasOfWorldsMod.MOD_ID)
  public static class StructureFeatures {

    @ObjectHolder(TestStructure.NAME)
    public static final TestStructure TEST;

    static {
      TEST = null;
    }
  }

  public static class ConfiguredStructureFeatures {

    public static Holder<ConfiguredStructureFeature<?, ?>> TEST;
  }

  public static class Tags {

    public static class BiomeTags {

      public static final TagKey<Biome> HAS_MAP = TagKey.create(Registry.BIOME_REGISTRY, Resource.locate("has_structure/map"));
    }
  }
}
