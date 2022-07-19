package com.codetaylor.mc.atlasofworlds.lib.level;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DefaultJigsawAssembler
    implements IJigsawAssembler {

  private static final Logger LOGGER = LogUtils.getLogger();

  private final IJigsawStructurePieceSelector jigsawStructurePieceSelector;
  private final Registry<StructureTemplatePool> registry;
  private final StructureManager structureManager;

  public DefaultJigsawAssembler(
      IJigsawStructurePieceSelector jigsawStructurePieceSelector,
      Registry<StructureTemplatePool> registry,
      StructureManager structureManager
  ) {

    this.jigsawStructurePieceSelector = jigsawStructurePieceSelector;
    this.registry = registry;
    this.structureManager = structureManager;
  }

  @Override
  public void assemble(
      AtlasMapConfiguration atlasMapConfiguration,
      PoolElementStructurePiece poolElementStructurePiece,
      MutableObject<VoxelShape> free,
      int depth,
      int maxDepth,
      boolean doBoundaryAdjustments,
      LevelHeightAccessor levelHeightAccessor,
      Deque<PieceState> placingQueue,
      List<PoolElementStructurePiece> pieceList,
      Random random
  ) {

    // Get piece data
    StructurePoolElement structurePoolElement = poolElementStructurePiece.getElement();
    BlockPos blockPos = poolElementStructurePiece.getPosition();
    Rotation rotation = poolElementStructurePiece.getRotation();
    BoundingBox boundingBox = poolElementStructurePiece.getBoundingBox();

    int minY = boundingBox.minY();

    // VoxelShape holder
    MutableObject<VoxelShape> mutableVoxelShape = new MutableObject<>();

    // Get a shuffled list of all jigsaw blocks in this piece
    List<StructureTemplate.StructureBlockInfo> shuffledJigsawBlocks = structurePoolElement.getShuffledJigsawBlocks(this.structureManager, blockPos, rotation, random);

    // For each jigsaw block in this piece, attempt to generate an attachment
    for (StructureTemplate.StructureBlockInfo structureBlockInfo : shuffledJigsawBlocks) {

      Direction direction = JigsawBlock.getFrontFacing(structureBlockInfo.state);
      BlockPos structureBlockPos = structureBlockInfo.pos;
      BlockPos structureRelativeBlockPos = structureBlockPos.relative(direction);

      ResourceLocation structureBlockPoolResourceLocation = new ResourceLocation(structureBlockInfo.nbt.getString("pool"));
      Optional<StructureTemplatePool> structureTemplatePoolOptional = this.registry.getOptional(structureBlockPoolResourceLocation);

      // If the pool is missing, log a warning and skip
      if (structureTemplatePoolOptional.isEmpty()) {
        LOGGER.warn("Non-existent pool: {}", structureBlockPoolResourceLocation);
        continue;
      }

      StructureTemplatePool structureTemplatePool = structureTemplatePoolOptional.get();

      // If the pool is empty, log a warning and skip
      if (structureTemplatePool.size() == 0 && !structureBlockPoolResourceLocation.equals(Pools.EMPTY.location())) {
        LOGGER.warn("Empty pool: {}", structureBlockPoolResourceLocation);
        continue;
      }

      ResourceLocation fallbackStructureTemplatePoolResourceLocation = structureTemplatePool.getFallback();
      Optional<StructureTemplatePool> fallbackStructureTemplatePoolOptional = this.registry.getOptional(fallbackStructureTemplatePoolResourceLocation);

      // If the pool is missing, log a warning and skip
      if (fallbackStructureTemplatePoolOptional.isEmpty()) {
        LOGGER.warn("Non-existent fallback pool: {}", fallbackStructureTemplatePoolResourceLocation);
        continue;
      }

      StructureTemplatePool fallbackStructureTemplatePool = structureTemplatePoolOptional.get();

      // If the pool is empty, log a warning and skip
      if (fallbackStructureTemplatePool.size() == 0 && !fallbackStructureTemplatePoolResourceLocation.equals(Pools.EMPTY.location())) {
        LOGGER.warn("Empty fallback pool: {}", fallbackStructureTemplatePoolResourceLocation);
        continue;
      }

      MutableObject<VoxelShape> voxelShape;

      if (boundingBox.isInside(structureRelativeBlockPos)) {
        voxelShape = mutableVoxelShape;

        if (voxelShape.getValue() == null) {
          voxelShape.setValue(Shapes.create(AABB.of(boundingBox)));
        }

      } else {
        voxelShape = free;
      }

      if (depth != maxDepth) {
        PoolElementStructurePiece selectedStructurePiece = this.jigsawStructurePieceSelector.select(
            atlasMapConfiguration,
            structureTemplatePool.rawTemplates,
            doBoundaryAdjustments,
            structureBlockInfo,
            structureRelativeBlockPos,
            minY,
            structureBlockPos,
            voxelShape,
            poolElementStructurePiece,
            depth,
            maxDepth,
            levelHeightAccessor,
            random
        );

        if (selectedStructurePiece != null) {

          // Add the new piece to the list
          pieceList.add(selectedStructurePiece);

          // Add the new piece to the placing queue if we haven't exceeded the max depth
          if (depth + 1 <= maxDepth) {
            placingQueue.addLast(new PieceState(selectedStructurePiece, voxelShape, depth + 1));
          }

          continue;
        }
      }

      PoolElementStructurePiece selectedStructurePiece = this.jigsawStructurePieceSelector.select(
          atlasMapConfiguration,
          fallbackStructureTemplatePool.rawTemplates,
          doBoundaryAdjustments,
          structureBlockInfo,
          structureRelativeBlockPos,
          minY,
          structureBlockPos,
          voxelShape,
          poolElementStructurePiece,
          depth,
          maxDepth,
          levelHeightAccessor,
          random
      );

      if (selectedStructurePiece != null) {

        // Add the new piece to the list
        pieceList.add(selectedStructurePiece);

        // Add the new piece to the placing queue if we haven't exceeded the max depth
        if (depth + 1 <= maxDepth) {
          placingQueue.addLast(new PieceState(selectedStructurePiece, voxelShape, depth + 1));
        }
      }
    }
  }
}
