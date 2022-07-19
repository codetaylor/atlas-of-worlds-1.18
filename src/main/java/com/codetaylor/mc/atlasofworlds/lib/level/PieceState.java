package com.codetaylor.mc.atlasofworlds.lib.level;

import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

record PieceState(PoolElementStructurePiece piece, MutableObject<VoxelShape> free, int depth) {
  //
}
