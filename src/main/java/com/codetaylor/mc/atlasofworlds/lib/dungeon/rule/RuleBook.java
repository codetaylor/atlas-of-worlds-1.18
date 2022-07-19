package com.codetaylor.mc.atlasofworlds.lib.dungeon.rule;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class RuleBook
    implements IRuleBook {

  private final List<IApplicationStrategy> applicationStrategyList;
  private final int min, max;

  private int index;
  private int iteration;

  private RuleBook(List<IApplicationStrategy> applicationStrategyList, int min, int max) {

    this.applicationStrategyList = applicationStrategyList;
    this.min = min;
    this.max = max;
  }

  @Override
  public void apply(Graph graph, Random random) {

    int iterations = Math.max(random.nextInt(this.max - this.min + 1) + this.min, 1);

    for (int i = 0; i < iterations; i++) {

      for (IApplicationStrategy strategy : this.applicationStrategyList) {
        strategy.apply(graph, random);
      }
    }
  }

  @Override
  public boolean applyNext(Graph graph, Random random) {

    if (this.iteration == 0) {
      this.iteration = Math.max(random.nextInt(this.max - this.min + 1) + this.min, 1);
    }

    IApplicationStrategy strategy = this.applicationStrategyList.get(this.index);

    if (strategy instanceof IRuleBook book) {

      if (!book.applyNext(graph, random)) {
        this.index += 1;
      }

    } else {

      strategy.apply(graph, random);
      this.index += 1;
    }

    if (this.index < this.applicationStrategyList.size()) {
      return true;

    } else {
      this.index = 0;
      this.iteration -= 1;

      return (this.iteration != 0);
    }
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private final List<IApplicationStrategy> applicationStrategyList;

    private int min, max;

    public Builder() {

      this.applicationStrategyList = new ArrayList<>();
    }

    public <E extends Element> ApplicationStrategyBuilder<E> random(AbstractApplicationStrategy.ElementType<E> elementType) {

      return new ApplicationStrategyBuilder<>(this, ApplicationStrategyBuilder.Type.Random, elementType, this.applicationStrategyList::add);
    }

    public <E extends Element> ApplicationStrategyBuilder<E> cellular(AbstractApplicationStrategy.ElementType<E> elementType) {

      return new ApplicationStrategyBuilder<>(this, ApplicationStrategyBuilder.Type.Cellular, elementType, this.applicationStrategyList::add);
    }

    public <E extends Element> ApplicationStrategyBuilder<E> lSystem(AbstractApplicationStrategy.ElementType<E> elementType) {

      return new ApplicationStrategyBuilder<>(this, ApplicationStrategyBuilder.Type.LSystem, elementType, this.applicationStrategyList::add);
    }

    public Builder iterations(int fixed) {

      this.min = fixed;
      this.max = fixed;
      return this;
    }

    public Builder iterations(int min, int max) {

      this.min = min;
      this.max = max;
      return this;
    }

    public Builder book(IRuleBook ruleBook) {

      this.applicationStrategyList.add(ruleBook);
      return this;
    }

    public IRuleBook build() {

      return new RuleBook(new ArrayList<>(this.applicationStrategyList), this.min, this.max);
    }

  }

  public static class ApplicationStrategyBuilder<E extends Element> {

    public enum Type {
      Random, LSystem, Cellular
    }

    private final Builder builder;
    private final Type type;
    private final AbstractApplicationStrategy.ElementType<E> elementType;
    private final Consumer<IApplicationStrategy> applicationStrategyConsumer;
    private IRule<E> rule;
    private int min, max;
    private boolean reverse;

    public ApplicationStrategyBuilder(Builder builder, Type type, AbstractApplicationStrategy.ElementType<E> elementType, Consumer<IApplicationStrategy> applicationStrategyConsumer) {

      this.builder = builder;
      this.type = type;
      this.elementType = elementType;
      this.applicationStrategyConsumer = applicationStrategyConsumer;
      this.rule = null;
      this.min = 1;
      this.max = 1;
      this.reverse = false;
    }

    public ApplicationStrategyBuilder<E> rule(IRule<E> rule) {

      this.rule = rule;
      return this;
    }

    public ApplicationStrategyBuilder<E> rule(Rule.Match<E> match, Rule.Apply apply, Rule.Apply... applies) {

      this.rule = new Rule<>(match, apply, applies);
      return this;
    }

    public ApplicationStrategyBuilder<E> iterations(int fixed) {

      this.min = fixed;
      this.max = fixed;
      return this;
    }

    public ApplicationStrategyBuilder<E> iterations(int min, int max) {

      this.min = min;
      this.max = max;
      return this;
    }

    public ApplicationStrategyBuilder<E> reverse() {

      this.reverse = true;
      return this;
    }

    public Builder add() {

      if (this.rule == null) {

        this.rule = IRule.noOp();
      }

      switch (this.type) {
        case Random -> this.applicationStrategyConsumer.accept(new ApplicationStrategy.Random<>(this.elementType, this.rule, this.min, this.max));
        case LSystem -> this.applicationStrategyConsumer.accept(new ApplicationStrategy.LSystem<>(this.elementType, this.rule, this.reverse, this.min, this.max));
        case Cellular -> this.applicationStrategyConsumer.accept(new ApplicationStrategy.Cellular<>(this.elementType, this.rule, this.reverse, this.min, this.max));
      }

      return this.builder;
    }
  }
}
