package com.codetaylor.mc.atlasofworlds.lib.level;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Deque;
import java.util.List;
import java.util.Random;

public interface IJigsawAssembler {

  void assemble(
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
  );
}
