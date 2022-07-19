package com.codetaylor.mc.atlasofworlds.lib.level;

import com.codetaylor.mc.atlasofworlds.lib.util.WeightedPicker;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class DefaultJigsawStructurePieceSelector
    implements IJigsawStructurePieceSelector {

  private final Registry<StructureTemplatePool> registry;
  private final JigsawPlacement.PieceFactory pieceFactory;
  private final ChunkGenerator chunkGenerator;
  private final StructureManager structureManager;

  public DefaultJigsawStructurePieceSelector(
      Registry<StructureTemplatePool> registry,
      JigsawPlacement.PieceFactory pieceFactory,
      ChunkGenerator chunkGenerator,
      StructureManager structureManager
  ) {

    this.registry = registry;
    this.pieceFactory = pieceFactory;
    this.chunkGenerator = chunkGenerator;
    this.structureManager = structureManager;
  }

  @Override
  public PoolElementStructurePiece select(
      AtlasMapConfiguration atlasMapConfiguration,
      List<Pair<StructurePoolElement, Integer>> rawTemplates,
      boolean doBoundaryAdjustments,
      StructureTemplate.StructureBlockInfo structureBlockInfo,
      BlockPos jigsawTargetBlockPos,
      int minY,
      BlockPos jigsawBlockPos,
      MutableObject<VoxelShape> voxelShape,
      PoolElementStructurePiece poolElementStructurePiece,
      int depth,
      int maxDepth,
      LevelHeightAccessor levelHeightAccessor,
      Random random
  ) {

    StructureTemplatePool.Projection placementBehavior = poolElementStructurePiece.getElement().getProjection();
    boolean isRigid = placementBehavior == StructureTemplatePool.Projection.RIGID;
    int jigsawBlockRelativeY = (jigsawBlockPos.getY() - minY);
    int surfaceHeight = -1;

    SelectionPool selectionPool = new SelectionPool(random);

    rawTemplates.forEach(pair -> {

      StructurePoolElement structurePoolElement = pair.getFirst();

      // Hook to allow veto of template
      if (this.allowStructurePoolElementForSelection(structurePoolElement, depth, maxDepth, atlasMapConfiguration)) {

        // Hook to override weight
        int weight = this.overrideStructurePoolElementSelectionWeight(structurePoolElement, pair.getSecond(), depth, maxDepth, atlasMapConfiguration);
        selectionPool.add(weight, structurePoolElement);
      }
    });

    while (selectionPool.size() > 0) {

      // Inject piece prioritization / selection override logic
      StructurePoolElement candidate = this.selectCandidate(selectionPool, depth, maxDepth, atlasMapConfiguration);

      // Abort if the selected piece is empty
      if (candidate == EmptyPoolElement.INSTANCE) {
        return null;
      }

      // TODO: inject candidate rejection (ie. test for max instances of a piece)
      // Can't we already do this during the creation of the selection pool above?

      for (Rotation rotation : Rotation.getShuffled(random)) {
        List<StructureTemplate.StructureBlockInfo> shuffledJigsawBlockList = candidate.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, rotation, random);

        // Get the candidate's bounding box
        BoundingBox tempCandidateBoundingBox = candidate.getBoundingBox(this.structureManager, BlockPos.ZERO, rotation);

        // Set height adjustments with respect to doBoundaryAdjustments
        int candidateHeightAdjustments;

        if (doBoundaryAdjustments && tempCandidateBoundingBox.getYSpan() <= 16) {

          candidateHeightAdjustments = shuffledJigsawBlockList.stream().mapToInt(value -> {

            if (!tempCandidateBoundingBox.isInside(value.pos.relative(JigsawBlock.getFrontFacing(value.state)))) {
              return 0;
            }

            ResourceLocation candidateTargetPool = new ResourceLocation(value.nbt.getString("pool"));
            Optional<StructureTemplatePool> candidateTargetPoolOptional = this.registry.getOptional(candidateTargetPool);
            Optional<StructureTemplatePool> candidateTargetFallbackOptional = candidateTargetPoolOptional.flatMap((StructureTemplatePool) -> this.registry.getOptional(StructureTemplatePool.getFallback()));
            int tallestCandidateTargetPoolPieceHeight = candidateTargetPoolOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.structureManager)).orElse(0);
            int tallestCandidateTargetFallbackPieceHeight = candidateTargetFallbackOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.structureManager)).orElse(0);
            return Math.max(tallestCandidateTargetPoolPieceHeight, tallestCandidateTargetFallbackPieceHeight);
          }).max().orElse(0);

        } else {
          candidateHeightAdjustments = 0;
        }

        // Check each jigsaw block for a match
        for (StructureTemplate.StructureBlockInfo candidateJigsawBlock : shuffledJigsawBlockList) {

          if (!JigsawBlock.canAttach(structureBlockInfo, candidateJigsawBlock)) {
            continue;
          }

          BlockPos candidateJigsawBlockPos = candidateJigsawBlock.pos;
          BlockPos candidateJigsawRelativeBlockPos = jigsawTargetBlockPos.subtract(candidateJigsawBlockPos);

          // Get the candidate's bounding box
          BoundingBox candidateBoundingBox = candidate.getBoundingBox(this.structureManager, candidateJigsawRelativeBlockPos, rotation);

          // Is the candidate rigid
          StructureTemplatePool.Projection candidatePlacementBehavior = candidate.getProjection();
          boolean isCandidateRigid = candidatePlacementBehavior == StructureTemplatePool.Projection.RIGID;

          // Calculate the candidate Y offset
          int candidateJigsawBlockRelativeY = candidateJigsawBlockPos.getY();
          int candidateJigsawYOffset = jigsawBlockRelativeY - candidateJigsawBlockRelativeY + JigsawBlock.getFrontFacing(structureBlockInfo.state).getStepY();

          int offsetCandidateMinY;

          if (isRigid && isCandidateRigid) {
            offsetCandidateMinY = (minY + candidateJigsawYOffset);

          } else {

            if (surfaceHeight == -1) {
              surfaceHeight = this.chunkGenerator.getFirstFreeHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor);
            }

            offsetCandidateMinY = surfaceHeight - candidateJigsawBlockRelativeY;
          }

          int candidatePieceYOffset = offsetCandidateMinY - candidateBoundingBox.minY();

          // Offset the candidate's bounding box and relative jigsaw block pos
          BoundingBox offsetCandidateBoundingBox = candidateBoundingBox.moved(0, candidatePieceYOffset, 0);
          BlockPos offsetCandidateJigsawRelativeBlockPos = candidateJigsawRelativeBlockPos.offset(0, candidatePieceYOffset, 0);

          // Apply the height adjustments
          if (candidateHeightAdjustments > 0) {
            int offsetY = Math.max(candidateHeightAdjustments + 1, offsetCandidateBoundingBox.maxY() - offsetCandidateBoundingBox.minY());
            offsetCandidateBoundingBox.encapsulate(new BlockPos(offsetCandidateBoundingBox.minX(), offsetCandidateBoundingBox.minY() + offsetY, offsetCandidateBoundingBox.minZ()));
          }

          // Boundary check
          if (this.shouldRejectCandidateBoundingBox(voxelShape.getValue(), offsetCandidateBoundingBox, atlasMapConfiguration)) {
            continue;
          }

          // Update the voxel shape
          voxelShape.setValue(Shapes.joinUnoptimized(voxelShape.getValue(), Shapes.create(AABB.of(offsetCandidateBoundingBox)), BooleanOp.ONLY_FIRST));

          int newGroundLevelDelta = poolElementStructurePiece.getGroundLevelDelta();
          int groundLevelDelta = (isCandidateRigid)
              ? newGroundLevelDelta - candidateJigsawYOffset
              : candidate.getGroundLevelDelta();

          // Create the new piece
          PoolElementStructurePiece newPoolElementStructurePiece = this.pieceFactory.create(
              this.structureManager,
              candidate,
              offsetCandidateJigsawRelativeBlockPos,
              groundLevelDelta,
              rotation,
              offsetCandidateBoundingBox
          );

          // Calculate Y for the new jigsaw block
          int candidateJigsawBlockY;

          if (isRigid) {
            candidateJigsawBlockY = minY + jigsawBlockRelativeY;

          } else if (isCandidateRigid) {
            candidateJigsawBlockY = (offsetCandidateMinY + candidateJigsawBlockRelativeY);

          } else {

            if (surfaceHeight == -1) {
              surfaceHeight = this.chunkGenerator.getFirstFreeHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor);
            }

            candidateJigsawBlockY = surfaceHeight + (candidateJigsawYOffset / 2);
          }

          // Add a junction to the existing piece
          poolElementStructurePiece.addJunction(
              new JigsawJunction(
                  jigsawTargetBlockPos.getX(),
                  candidateJigsawBlockY - jigsawBlockRelativeY + newGroundLevelDelta,
                  jigsawTargetBlockPos.getZ(),
                  candidateJigsawYOffset,
                  candidatePlacementBehavior
              )
          );

          // Add a junction to the new piece
          newPoolElementStructurePiece.addJunction(
              new JigsawJunction(
                  jigsawBlockPos.getX(),
                  candidateJigsawBlockY - candidateJigsawBlockRelativeY + groundLevelDelta,
                  jigsawBlockPos.getZ(),
                  -candidateJigsawYOffset,
                  placementBehavior
              )
          );

          this.onCandidateSelected(candidate, newPoolElementStructurePiece, atlasMapConfiguration);

          return newPoolElementStructurePiece;
        }
      }
    }

    return null;
  }

  // ---------------------------------------------------------------------------
  // Selection Pool
  // ---------------------------------------------------------------------------

  private static class SelectionPool
      implements Iterable<StructurePoolElement> {

    private final WeightedPicker<StructurePoolElement> weightedPicker;
    private final List<StructurePoolElement> list;

    public SelectionPool(Random random) {

      this.weightedPicker = new WeightedPicker<>(random);
      this.list = new ArrayList<>();
    }

    public int size() {

      return this.list.size();
    }

    public void add(int weight, StructurePoolElement element) {

      this.list.add(element);
      this.weightedPicker.add(weight, element);
    }

    public StructurePoolElement selectRandom() {

      return this.weightedPicker.get();
    }

    public StructurePoolElement remove(StructurePoolElement element) {

      this.weightedPicker.remove(element);
      this.list.remove(element);
      return element;
    }

    @NotNull
    @Override
    public Iterator<StructurePoolElement> iterator() {

      return new Itr(this.list.iterator(), this.weightedPicker);
    }

    private class Itr
        implements Iterator<StructurePoolElement> {

      private final Iterator<StructurePoolElement> iterator;
      private final WeightedPicker<StructurePoolElement> weightedPicker;

      private StructurePoolElement lastElementReturned;

      public Itr(Iterator<StructurePoolElement> iterator, WeightedPicker<StructurePoolElement> weightedPicker) {

        this.iterator = iterator;
        this.weightedPicker = weightedPicker;
      }

      @Override
      public boolean hasNext() {

        return this.iterator.hasNext();
      }

      @Override
      public StructurePoolElement next() {

        this.lastElementReturned = this.iterator.next();
        return this.lastElementReturned;
      }

      @Override
      public void remove() {

        this.iterator.remove();
        this.weightedPicker.remove(this.lastElementReturned);
      }

      @Override
      public void forEachRemaining(Consumer<? super StructurePoolElement> action) {

        this.iterator.forEachRemaining(action);
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Behavioral Overrides
  // ---------------------------------------------------------------------------

  /**
   * Called when constructing the selection pool of {@link StructurePoolElement}
   * to determine if the given element is allowed to be added to the selection pool.
   *
   * @param structurePoolElement  the element
   * @param depth                 the current generation depth
   * @param maxDepth              the maximum generation depth
   * @param atlasMapConfiguration the map config
   * @return true if the element should be added to the pool
   */
  protected boolean allowStructurePoolElementForSelection(StructurePoolElement structurePoolElement, int depth, int maxDepth, AtlasMapConfiguration atlasMapConfiguration) {

    return true;
  }

  /**
   * Called to allow overriding the weight for the given element before adding
   * to the selection pool.
   *
   * @param structurePoolElement  the element
   * @param weight                the element's weight
   * @param depth                 the current generation depth
   * @param maxDepth              the maximum generation depth
   * @param atlasMapConfiguration the map config
   * @return the element's selection weight
   */
  protected int overrideStructurePoolElementSelectionWeight(StructurePoolElement structurePoolElement, int weight, int depth, int maxDepth, AtlasMapConfiguration atlasMapConfiguration) {

    return weight;
  }

  /**
   * Called to select a candidate from the selection pool.
   * <p>
   * Note: The selected element must be removed from the pool.
   *
   * @param selectionPool         the selection pool
   * @param depth                 the current generation depth
   * @param maxDepth              the maximum generation depth
   * @param atlasMapConfiguration the map config
   * @return the selected candidate
   */
  protected StructurePoolElement selectCandidate(SelectionPool selectionPool, int depth, int maxDepth, AtlasMapConfiguration atlasMapConfiguration) {

    StructurePoolElement structurePoolElement = selectionPool.selectRandom();
    selectionPool.remove(structurePoolElement);
    return structurePoolElement;
  }

  /**
   * Called after all calculations have been applied to the candidate's bounding
   * box to determine if it should be rejected.
   *
   * @param voxelShape
   * @param candidateBoundingBox  the candidate's bounding box
   * @param atlasMapConfiguration the map config
   * @return true if the candidate should be rejected
   */
  protected boolean shouldRejectCandidateBoundingBox(VoxelShape voxelShape, BoundingBox candidateBoundingBox, AtlasMapConfiguration atlasMapConfiguration) {

    return Shapes.joinIsNotEmpty(voxelShape, Shapes.create(AABB.of(candidateBoundingBox).deflate(0.25)), BooleanOp.ONLY_SECOND);
  }

  /**
   * Called when a candidate is successfully selected and added to the assembly.
   *
   * @param candidate             the selected candidate
   * @param piece                 the selected piece generated from the candidate
   * @param atlasMapConfiguration the map config
   */
  protected void onCandidateSelected(StructurePoolElement candidate, PoolElementStructurePiece piece, AtlasMapConfiguration atlasMapConfiguration) {
    //
  }
}
