package com.codetaylor.mc.atlasofworlds.lib.level;

import com.google.common.collect.Queues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class PieceGeneratorFactory<C extends JigsawConfiguration> {

  private final IJigsawAssembler jigsawAssembler;

  public PieceGeneratorFactory(IJigsawAssembler jigsawAssembler) {

    this.jigsawAssembler = jigsawAssembler;
  }

  public Optional<PieceGenerator<C>> create(
      PieceGeneratorSupplier.Context<C> context,
      JigsawPlacement.PieceFactory pieceFactory,
      BlockPos blockPos,
      boolean doBoundaryAdjustments,
      boolean useHeightmap
  ) {

    WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0));
    worldgenRandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);

    return this.create(
        worldgenRandom,
        context.config(),
        context.chunkGenerator(),
        context.structureManager(),
        context.heightAccessor(),
        context.validBiome(),
        pieceFactory,
        blockPos,
        doBoundaryAdjustments,
        useHeightmap
    );
  }

  public Optional<PieceGenerator<C>> create(
      Random random,
      C jigsawConfiguration,
      ChunkGenerator chunkGenerator,
      StructureManager structureManager,
      LevelHeightAccessor levelHeightAccessor,
      Predicate<Holder<Biome>> biomePredicate,
      JigsawPlacement.PieceFactory pieceFactory,
      BlockPos blockPos,
      boolean doBoundaryAdjustments,
      boolean useHeightmap
  ) {

    // Get a random rotation for the starting piece
    Rotation rotation = Rotation.getRandom(random);

    // Get the starting template pool
    StructureTemplatePool structureTemplatePool = jigsawConfiguration.startPool().value();

    // Get a random template from the starting pool
    StructurePoolElement structurePoolElement = structureTemplatePool.getRandomTemplate(random);

    // If there are no starting templates, return empty
    if (structurePoolElement == EmptyPoolElement.INSTANCE) {
      return Optional.empty();
    }

    // Generate a piece using the template
    PoolElementStructurePiece poolElementStructurePiece = pieceFactory.create(
        structureManager,
        structurePoolElement,
        blockPos,
        structurePoolElement.getGroundLevelDelta(),
        rotation,
        structurePoolElement.getBoundingBox(
            structureManager,
            blockPos,
            rotation
        )
    );

    // Calculate the center position of the starting piece's bounding box
    BoundingBox boundingbox = poolElementStructurePiece.getBoundingBox();
    int centerX = (boundingbox.maxX() + boundingbox.minX()) / 2;
    int centerZ = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
    int centerY = (useHeightmap)
        ? blockPos.getY() + chunkGenerator.getFirstFreeHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor)
        : blockPos.getY();

    // Perform a biome check using the center position
    if (!biomePredicate.test(chunkGenerator.getNoiseBiome(QuartPos.fromBlock(centerX), QuartPos.fromBlock(centerY), QuartPos.fromBlock(centerZ)))) {
      return Optional.empty();
    }

    // TODO: Is this necessary for us since we will likely never be using terrain height?
    int adjustedY = boundingbox.minY() + poolElementStructurePiece.getGroundLevelDelta();
    poolElementStructurePiece.move(0, centerY - adjustedY, 0);

    return Optional.of(new AtlasPieceGenerator<>(
        poolElementStructurePiece,
        new BlockPos(centerX, centerY, centerZ),
        jigsawConfiguration,
        this.jigsawAssembler,
        doBoundaryAdjustments,
        boundingbox,
        levelHeightAccessor,
        random
    ));
  }

  private static class AtlasPieceGenerator<C extends JigsawConfiguration>
      implements PieceGenerator<C>,
      IAtlasPieceGenerator {

    private final PoolElementStructurePiece poolElementStructurePiece;
    private final BlockPos center;
    private final C configuration;
    private final IJigsawAssembler jigsawAssembler;
    private final boolean doBoundaryAdjustments;
    private final BoundingBox boundingBox;
    private final LevelHeightAccessor levelHeightAccessor;
    private final Random random;

    private AtlasMapConfiguration atlasMapConfiguration;

    private AtlasPieceGenerator(PoolElementStructurePiece poolElementStructurePiece, BlockPos center, C configuration, IJigsawAssembler jigsawAssembler, boolean doBoundaryAdjustments, BoundingBox boundingBox, LevelHeightAccessor levelHeightAccessor, Random random) {

      this.poolElementStructurePiece = poolElementStructurePiece;
      this.center = center;
      this.configuration = configuration;
      this.jigsawAssembler = jigsawAssembler;
      this.doBoundaryAdjustments = doBoundaryAdjustments;
      this.boundingBox = boundingBox;
      this.levelHeightAccessor = levelHeightAccessor;
      this.random = random;
    }

    @Override
    public void setAtlasMapConfiguration(AtlasMapConfiguration atlasMapConfiguration) {

      this.atlasMapConfiguration = atlasMapConfiguration;
    }

    @ParametersAreNonnullByDefault
    public void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<C> context) {

      List<PoolElementStructurePiece> resultList = Lists.newArrayList();

      // Add the starting piece to the result list
      resultList.add(this.poolElementStructurePiece);

      if (this.configuration.maxDepth() > 0) {
        AABB aabb = new AABB(this.center.getX() - 80, this.center.getY() - 80, this.center.getZ() - 80, this.center.getX() + 81, this.center.getY() + 81, this.center.getZ() + 81);

        Deque<PieceState> placingQueue = Queues.newArrayDeque();
        placingQueue.addLast(new PieceState(this.poolElementStructurePiece, new MutableObject<>(Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(this.boundingBox)), BooleanOp.ONLY_FIRST)), 0));

        while (!placingQueue.isEmpty()) {
          PieceState pieceState = placingQueue.removeFirst();
          this.jigsawAssembler.assemble(this.atlasMapConfiguration, pieceState.piece(), pieceState.free(), pieceState.depth(), this.configuration.maxDepth(), this.doBoundaryAdjustments, this.levelHeightAccessor, placingQueue, resultList, this.random);
        }

        resultList.forEach(builder::addPiece);
      }
    }
  }

}
