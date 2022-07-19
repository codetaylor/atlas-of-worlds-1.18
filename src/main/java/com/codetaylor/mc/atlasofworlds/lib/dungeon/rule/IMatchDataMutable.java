package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;

public interface IMatchDataMutable
    extends IMatchData {

  IMatchDataMutable add(Element element);
}
