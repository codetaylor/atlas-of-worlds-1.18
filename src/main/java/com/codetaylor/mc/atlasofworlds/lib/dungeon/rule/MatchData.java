package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchData
    implements IMatchDataMutable {

  private final List<Element> list;

  public MatchData() {

    this.list = new ArrayList<>(3);
  }

  public MatchData(Element element, Element... elements) {

    this.list = new ArrayList<>(1 + elements.length);
    this.list.add(element);

    if (elements.length > 0) {
      this.list.addAll(Arrays.asList(elements));
    }
  }

  @Override
  public <E extends Element> E get(int index) {

    if (index >= this.list.size()) {
      return null;
    }

    //noinspection unchecked
    return (E) this.list.get(index);
  }

  @Override
  public IMatchDataMutable add(Element element) {

    this.list.add(element);
    return this;
  }
}
