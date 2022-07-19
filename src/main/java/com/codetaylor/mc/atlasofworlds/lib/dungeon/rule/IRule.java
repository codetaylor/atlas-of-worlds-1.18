package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;

import java.util.Random;

public interface IRule<E extends Element> {

  void match(Graph graph, E element, MatchDataList matches);

  void apply(Graph graph, IMatchData match, Random random);

  static <E extends Element> IRule<E> noOp() {

    return new IRule<>() {

      @Override
      public void match(Graph graph, E element, MatchDataList matches) {
        //
      }

      @Override
      public void apply(Graph graph, IMatchData match, Random random) {
        //
      }
    };
  }
}
