package com.codetaylor.mc.atlasofworlds.lib.util;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public final class StructureHelper {

  public static <C extends FeatureConfiguration> PieceGeneratorSupplier.Context<C> createPieceGeneratorContextWithConfig(PieceGeneratorSupplier.Context<C> context, C newConfig) {

    return new PieceGeneratorSupplier.Context<>(
        context.chunkGenerator(),
        context.biomeSource(),
        context.seed(),
        context.chunkPos(),
        newConfig,
        context.heightAccessor(),
        context.validBiome(),
        context.structureManager(),
        context.registryAccess()
    );
  }

  private StructureHelper() {
    //
  }
}
