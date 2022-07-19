package com.codetaylor.mc.atlasofworlds.lib.dungeon.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Element {

  protected final Map<String, Object> attributeMap;

  public Element() {

    this.attributeMap = new HashMap<>(16);
  }

  public boolean hasAttribute(String name) {

    return this.attributeMap.containsKey(name);
  }

  public boolean hasAttribute(String name, Class<?> aClass) {

    return aClass.isInstance(this.attributeMap.get(name));
  }

  public Object getAttribute(String name) {

    return this.attributeMap.get(name);
  }

  public <A> A getAttribute(String name, Class<A> aClass) {

    Object o = this.attributeMap.get(name);

    if (aClass.isInstance(o)) {
      return aClass.cast(o);
    }

    return null;
  }

  public Stream<String> attributeKeys() {

    return this.attributeMap.keySet().stream();
  }

  public int getAttributeCount() {

    return this.attributeMap.size();
  }

  public void clearAttributes() {

    this.attributeMap.clear();
  }

  public void setAttribute(String name, Object... values) {

    Object value;

    if (values == null) {
      value = null;

    } else if (values.length == 0) {
      value = true;

    } else if (values.length == 1) {
      value = values[0];

    } else {
      value = values;
    }

    this.attributeMap.put(name, value);
  }

  public void removeAttribute(String name) {

    this.attributeMap.remove(name);
  }
}
