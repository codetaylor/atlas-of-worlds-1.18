package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MatchDataList {

  private final List<IMatchDataMutable> list;

  public MatchDataList(List<IMatchDataMutable> list) {

    this.list = list;
  }

  public void add(Element element, Element... elements) {

    this.list.add(new MatchData(element, elements));
  }

  public void reverse() {

    Collections.reverse(this.list);
  }

  public int size() {

    return this.list.size();
  }

  public List<IMatchDataMutable> asList() {

    return this.list;
  }

  public boolean isEmpty() {

    return this.list.isEmpty();
  }

  public void shuffle(Random random) {

    Collections.shuffle(this.list, random);
  }
}
