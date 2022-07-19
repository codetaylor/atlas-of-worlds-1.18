package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Edge;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Node;

import java.util.stream.Stream;

public abstract class AbstractApplicationStrategy<E extends Element>
    implements IApplicationStrategy {

  protected final ElementType<E> elementType;
  protected final IRule<E> rule;
  protected final int min, max;

  public AbstractApplicationStrategy(ElementType<E> elementType, IRule<E> rule, int min, int max) {

    this.elementType = elementType;
    this.rule = rule;
    this.min = min;
    this.max = max;
  }

  public interface ElementType<E extends Element> {

    ElementType<Node> NODE = Graph::nodes;
    ElementType<Edge> EDGE = Graph::edges;

    Stream<E> get(Graph graph);
  }
}