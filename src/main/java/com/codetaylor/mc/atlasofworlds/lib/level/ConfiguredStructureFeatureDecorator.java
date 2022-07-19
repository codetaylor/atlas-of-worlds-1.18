package com.codetaylor.mc.atlasofworlds.lib.level;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Decorates a {@link ConfiguredStructureFeature} to provide an alternative
 * generate method that accepts an {@link AtlasMapConfiguration}.
 *
 * @param <FC> the {@link FeatureConfiguration}
 * @param <F>  the {@link StructureFeature}
 */
public class ConfiguredStructureFeatureDecorator<FC extends FeatureConfiguration, F extends StructureFeature<FC>> {

  protected final ConfiguredStructureFeature<FC, F> configuredStructureFeature;

  public static <FC extends FeatureConfiguration, F extends StructureFeature<FC>> ConfiguredStructureFeatureDecorator<FC, F> decorate(ConfiguredStructureFeature<FC, F> configuredStructureFeature) {

    return new ConfiguredStructureFeatureDecorator<>(configuredStructureFeature);
  }

  private ConfiguredStructureFeatureDecorator(ConfiguredStructureFeature<FC, F> configuredStructureFeature) {

    this.configuredStructureFeature = configuredStructureFeature;
  }

  /**
   * Returns a {@link StructureStart} for the wrapped {@link ConfiguredStructureFeature}.
   * <p>
   * This method is similar to the {@link ConfiguredStructureFeature#generate(RegistryAccess, ChunkGenerator, BiomeSource, StructureManager, long, ChunkPos, int, LevelHeightAccessor, Predicate)}
   * method, with the exception that this method accepts an additional {@link AtlasMapConfiguration}
   * parameter.
   * <p>
   * If the {@link PieceGenerator} provided by the {@link ConfiguredStructureFeature}
   * is an instance of {@link IAtlasPieceGenerator}, then the given {@link AtlasMapConfiguration}
   * will be provided to the generator before the structure pieces are generated.
   *
   * @param registryAccess
   * @param chunkGenerator
   * @param biomeSource
   * @param structureManager
   * @param seed
   * @param chunkPos
   * @param references
   * @param levelHeightAccessor
   * @param biomePredicate
   * @param atlasMapConfiguration
   * @return a {@link StructureStart}
   */
  public StructureStart generate(
      RegistryAccess registryAccess,
      ChunkGenerator chunkGenerator,
      BiomeSource biomeSource,
      StructureManager structureManager,
      long seed,
      ChunkPos chunkPos,
      int references,
      LevelHeightAccessor levelHeightAccessor,
      Predicate<Holder<Biome>> biomePredicate,
      AtlasMapConfiguration atlasMapConfiguration
  ) {

    PieceGeneratorSupplier.Context<FC> pieceGeneratorSupplierContext = new PieceGeneratorSupplier.Context<>(chunkGenerator, biomeSource, seed, chunkPos, this.configuredStructureFeature.config, levelHeightAccessor, biomePredicate, structureManager, registryAccess);
    Optional<PieceGenerator<FC>> optionalPieceGenerator = this.configuredStructureFeature.feature.pieceGeneratorSupplier().createGenerator(pieceGeneratorSupplierContext);

    if (optionalPieceGenerator.isPresent()) {
      StructurePiecesBuilder structurePiecesBuilder = new StructurePiecesBuilder();
      WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenRandom.setLargeFeatureSeed(seed, chunkPos.x, chunkPos.z);
      PieceGenerator.Context<FC> pieceGeneratorContext = new PieceGenerator.Context<>(this.configuredStructureFeature.config, chunkGenerator, structureManager, chunkPos, levelHeightAccessor, worldgenRandom, seed);
      PieceGenerator<FC> pieceGenerator = optionalPieceGenerator.get();

      if (pieceGenerator instanceof IAtlasPieceGenerator atlasPieceGenerator) {
        atlasPieceGenerator.setAtlasMapConfiguration(atlasMapConfiguration);
      }

      pieceGenerator.generatePieces(structurePiecesBuilder, pieceGeneratorContext);
      StructureStart structureStart = new StructureStart(this.configuredStructureFeature, chunkPos, references, structurePiecesBuilder.build());

      if (structureStart.isValid()) {
        return structureStart;
      }
    }

    return StructureStart.INVALID_START;
  }
}
