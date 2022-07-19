package com.codetaylor.mc.atlasofworlds.atlas.common.level.structure;

import com.codetaylor.mc.atlasofworlds.Resource;
import com.codetaylor.mc.atlasofworlds.lib.level.AtlasMapConfiguration;
import com.codetaylor.mc.atlasofworlds.lib.level.DefaultJigsawAssembler;
import com.codetaylor.mc.atlasofworlds.lib.level.DefaultJigsawStructurePieceSelector;
import com.codetaylor.mc.atlasofworlds.lib.level.PieceGeneratorFactory;
import com.codetaylor.mc.atlasofworlds.lib.util.StructureHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import javax.annotation.Nonnull;
import java.util.Optional;

public class TestStructure
    extends StructureFeature<JigsawConfiguration> {

  public static final String NAME = "test";

  public static final Codec<JigsawConfiguration> CODEC = RecordCodecBuilder.create((builder) -> builder
      .group(
          StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(JigsawConfiguration::startPool),
          Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(JigsawConfiguration::maxDepth)
      ).apply(builder, JigsawConfiguration::new)
  );

  public TestStructure() {

    super(CODEC, TestStructure::createPieceGenerator, PostPlacementProcessor.NONE);
  }

  @Nonnull
  @Override
  public GenerationStep.Decoration step() {

    return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
  }

  @Nonnull
  public static Optional<PieceGenerator<JigsawConfiguration>> createPieceGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {

//    if (!context.chunkPos().equals(ChunkPos.ZERO)) {
//      return Optional.empty();
//    }

    BlockPos blockPos = context.chunkPos().getMiddleBlockPosition(128); // TODO: hardcoded Y value

    Registry<StructureTemplatePool> templatePoolRegistry = context.registryAccess().registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);

    PieceGeneratorFactory<JigsawConfiguration> pieceGeneratorFactory = new PieceGeneratorFactory<>(
        new DefaultJigsawAssembler(
            new JigsawStructurePieceSelector(
                templatePoolRegistry,
                PoolElementStructurePiece::new,
                context.chunkGenerator(),
                context.structureManager()
            ),
            templatePoolRegistry,
            context.structureManager()
        )
    );

    JigsawConfiguration newConfiguration = new JigsawConfiguration(
        Holder.direct(context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(Resource.locate("test/start"))),
        7
    );

    PieceGeneratorSupplier.Context<JigsawConfiguration> newContext = StructureHelper.createPieceGeneratorContextWithConfig(context, newConfiguration);

    return pieceGeneratorFactory.create(newContext, PoolElementStructurePiece::new, blockPos, false, false);
  }

  public static final class JigsawStructurePieceSelector
      extends DefaultJigsawStructurePieceSelector {

    public JigsawStructurePieceSelector(
        Registry<StructureTemplatePool> registry,
        JigsawPlacement.PieceFactory pieceFactory,
        ChunkGenerator chunkGenerator,
        StructureManager structureManager
    ) {

      super(registry, pieceFactory, chunkGenerator, structureManager);
    }

    @Override
    protected boolean allowStructurePoolElementForSelection(StructurePoolElement structurePoolElement, int depth, int maxDepth, AtlasMapConfiguration atlasMapConfiguration) {

      return super.allowStructurePoolElementForSelection(structurePoolElement, depth, maxDepth, atlasMapConfiguration);
    }
  }
}