package com.codetaylor.mc.atlasofworlds.lib.dungeon.display;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;

import java.util.function.Supplier;

public interface IGraphGenerator
    extends Supplier<Graph> {

  void regenerateGraph();
}
