package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ApplicationStrategy {

  /**
   * Collects all the matches and applies transformation in order or reversed.
   *
   * @param <E> element type
   */
  public static class Cellular<E extends Element>
      extends AbstractApplicationStrategy<E> {

    protected final boolean reverse;

    public Cellular(ElementType<E> elementType, IRule<E> rule, boolean reverse, int min, int max) {

      super(elementType, rule, min, max);
      this.reverse = reverse;
    }

    @Override
    public void apply(Graph graph, java.util.Random random) {

      MatchDataList matchDataList = new MatchDataList(new ArrayList<>());
      this.elementType.get(graph).forEach(node -> this.rule.match(graph, node, matchDataList));

      if (this.reverse) {
        matchDataList.reverse();
      }

      int applicationCount = Math.min(random.nextInt(this.max - this.min + 1) + this.min, matchDataList.size());

      for (int i = 0; i < applicationCount; i++) {
        this.rule.apply(graph, matchDataList.asList().get(i), random);
      }
    }
  }

  public static class LSystem<E extends Element>
      extends AbstractApplicationStrategy<E> {

    protected final boolean reverse;

    public LSystem(ElementType<E> elementType, IRule<E> rule, boolean reverse, int min, int max) {

      super(elementType, rule, min, max);
      this.reverse = reverse;
    }

    @Override
    public void apply(Graph graph, java.util.Random random) {

      List<E> elementList = this.elementType.get(graph).collect(Collectors.toCollection(ArrayList::new));

      if (this.reverse) {
        Collections.reverse(elementList);
      }

      int applicationCount = Math.min(random.nextInt(this.max - this.min + 1) + this.min, elementList.size());

      for (E element : elementList) {
        MatchDataList matchDataList = new MatchDataList(new ArrayList<>());
        this.rule.match(graph, element, matchDataList);

        for (IMatchDataMutable data : matchDataList.asList()) {
          this.rule.apply(graph, data, random);
        }

        if (!matchDataList.isEmpty()) {
          applicationCount -= 1;

          if (applicationCount == 0) {
            break;
          }
        }
      }
    }
  }

  /**
   * Collects matches on each iteration and selects a random match to transform.
   *
   * @param <E> element type
   */
  public static class Random<E extends Element>
      extends AbstractApplicationStrategy<E> {

    public Random(ElementType<E> elementType, IRule<E> rule, int min, int max) {

      super(elementType, rule, min, max);
    }

    @Override
    public void apply(Graph graph, java.util.Random random) {

      int applicationCount = random.nextInt(this.max - this.min + 1) + this.min;

      for (int i = 0; i < applicationCount; i++) {
        MatchDataList matchDataList = new MatchDataList(new ArrayList<>());
        this.elementType.get(graph).forEach(e -> this.rule.match(graph, e, matchDataList));

        if (matchDataList.isEmpty()) {
          break;
        }

        matchDataList.shuffle(random);
        this.rule.apply(graph, matchDataList.asList().get(0), random);
      }
    }
  }

  private ApplicationStrategy() {
    //
  }
}
