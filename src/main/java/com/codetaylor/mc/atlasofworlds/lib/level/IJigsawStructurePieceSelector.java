package com.codetaylor.mc.atlasofworlds.lib.level;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Random;

public interface IJigsawStructurePieceSelector {

  PoolElementStructurePiece select(
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
      int maxDepth, LevelHeightAccessor levelHeightAccessor,
      Random random
  );
}
