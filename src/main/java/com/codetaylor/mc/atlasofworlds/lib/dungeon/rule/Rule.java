package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;

import java.util.*;

public class Rule<E extends Element>
    implements IRule<E> {

  protected final Match<E> match;
  protected final List<Apply> apply;

  public Rule(Match<E> match, Apply apply, Apply... applies) {

    this.match = match;

    if (applies.length == 0) {
      this.apply = Collections.singletonList(apply);

    } else {
      this.apply = new ArrayList<>(1 + applies.length);
      this.apply.add(apply);
      this.apply.addAll(Arrays.asList(applies));
    }
  }

  @Override
  public void match(Graph graph, E element, MatchDataList matches) {

    this.match.match(graph, element, matches);
  }

  @Override
  public void apply(Graph graph, IMatchData matchData, Random random) {

    this.apply.get(random.nextInt(this.apply.size())).apply(graph, matchData);
  }

  public interface Match<E extends Element> {

    void match(Graph g, E e, MatchDataList m);
  }

  public interface Apply {

    void apply(Graph g, IMatchData match);
  }
}
