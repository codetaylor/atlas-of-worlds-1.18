package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;

import java.util.Random;

public interface IRuleBook
    extends IApplicationStrategy {

  boolean applyNext(Graph graph, Random random);
}
