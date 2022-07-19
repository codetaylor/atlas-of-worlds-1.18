package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.Resource;
import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class FMLCommonSetupEventHandler {

  @SubscribeEvent
  public void on(FMLCommonSetupEvent event) {

    event.enqueueWork(() -> {
      FMLCommonSetupEventHandler.this.registerConfiguredStructures();
    });
  }

  private void registerConfiguredStructures() {

    JigsawConfiguration configuration = new JigsawConfiguration(PlainVillagePools.START, 0);
    ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> configuredStructureFeature = AtlasModule.StructureFeatures.TEST.configured(configuration, AtlasModule.Tags.BiomeTags.HAS_MAP);
    ResourceKey<ConfiguredStructureFeature<?, ?>> resourceKey = ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, Resource.locate("test"));
    AtlasModule.ConfiguredStructureFeatures.TEST = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resourceKey, configuredStructureFeature);
  }
}
