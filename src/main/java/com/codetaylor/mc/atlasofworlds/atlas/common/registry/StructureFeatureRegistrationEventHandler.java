package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.atlas.common.level.structure.TestStructure;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public record StructureFeatureRegistrationEventHandler() {

  @SubscribeEvent
  public void register(RegistryEvent.Register<StructureFeature<?>> event) {

    IForgeRegistry<StructureFeature<?>> registry = event.getRegistry();

    registry.register(new TestStructure().setRegistryName(TestStructure.NAME));
  }
}
