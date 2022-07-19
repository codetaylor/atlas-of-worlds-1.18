package com.codetaylor.mc.atlasofworlds.lib.dungeon.display;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Edge;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Node;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.rule.AbstractApplicationStrategy;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.rule.IRuleBook;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.rule.RuleBook;

import java.util.Random;

public class GraphGenerator
    implements IGraphGenerator {

  private Graph graph;

  private final int width;
  private final int height;
  private final Random random;

  public GraphGenerator(int width, int height, Random random) {

    this.width = width;
    this.height = height;
    this.random = random;
  }

  @Override
  public Graph get() {

    if (this.graph == null) {
      this.regenerateGraph();
    }

    return this.graph;
  }

  @Override
  public void regenerateGraph() {

    this.graph = this.generateGraph(this.width, this.height, this.random);
  }

  public Graph generateGraph(int width, int height, Random random) {

    Graph graph = new Graph(width * height);

    for (int x = 0; x < width; x++) {
      for (int z = 0; z < height; z++) {
        Node node = new Node(x, 0, z);
        graph.addNode(node);
      }
    }

    graph.nodes().forEach(node -> {
      // Add an edge to each spatially adjacent node if one doesn't exist.

      Node.Position position = node.getPosition();

      // North
      Node nodeNorth = graph.getNode(position.x(), position.y(), position.z() + 1);

      if (nodeNorth != null) {
        Edge edge = graph.getEdge(node, nodeNorth);

        if (edge == null) {
          graph.addEdge(new Edge(node, nodeNorth, false));
        }
      }

      // East
      Node nodeEast = graph.getNode(position.x() + 1, position.y(), position.z());

      if (nodeEast != null) {
        Edge edge = graph.getEdge(node, nodeEast);

        if (edge == null) {
          graph.addEdge(new Edge(node, nodeEast, false));
        }
      }

      // South
      Node nodeSouth = graph.getNode(position.x(), position.y(), position.z() - 1);

      if (nodeSouth != null) {
        Edge edge = graph.getEdge(node, nodeSouth);

        if (edge == null) {
          graph.addEdge(new Edge(node, nodeSouth, false));
        }
      }

      // West
      Node nodeWest = graph.getNode(position.x() - 1, position.y(), position.z());

      if (nodeWest != null) {
        Edge edge = graph.getEdge(node, nodeWest);

        if (edge == null) {
          graph.addEdge(new Edge(node, nodeWest, false));
        }
      }
    });

    // ---------------------------------------------------------------------------
    // Rules
    // ---------------------------------------------------------------------------

    IRuleBook ruleBook = RuleBook.builder()

        .random(AbstractApplicationStrategy.ElementType.NODE)
        .rule(
            (g, e, m) -> {
              if (g.edges(e).count() < 4 && e.getX() == 0) {
                m.add(e);
              }
            },
            (g, match) -> match.get(0).setAttribute("start")
        )
        .add()

        .random(AbstractApplicationStrategy.ElementType.NODE)
        .rule(
            (g, e, m) -> {
              if (g.edges(e).map(edge -> edge.getOpposite(e)).anyMatch(node -> node.hasAttribute("start") && node.getX() < e.getX())) {
                m.add(e);
              }
            },
            (g, match) -> match.get(0).setAttribute("path")
        )
        .add()

        .random(AbstractApplicationStrategy.ElementType.NODE)
        .iterations(3, 5)
        .rule(
            (g, e, m) -> {
              if (e.attributeKeys().findAny().isEmpty()) {
                g.edges(e)
                    .map(edge -> edge.getOpposite(e))
                    .filter(node -> node.hasAttribute("path") && node.getX() < e.getX())
                    .findFirst()
                    .ifPresent(node -> m.add(e, node));
              }
            },
            (g, match) -> {
              match.get(0).setAttribute("path");
              match.get(1).removeAttribute("path");
              match.get(1).setAttribute("path-resolved");
            }
        )
        .add()

        .random(AbstractApplicationStrategy.ElementType.NODE)
        .iterations(2, 3)
        .rule(
            (g, e, m) -> {
              if (e.attributeKeys().findAny().isEmpty()) {
                g.edges(e)
                    .map(edge -> edge.getOpposite(e))
                    .filter(node -> node.hasAttribute("path") && node.getX() == e.getX())
                    .findFirst()
                    .ifPresent(node -> m.add(e, node));
              }
            },
            (g, match) -> {
              match.get(0).setAttribute("path");
              match.get(1).removeAttribute("path");
              match.get(1).setAttribute("path-resolved");
            }
        )
        .add()

        .random(AbstractApplicationStrategy.ElementType.NODE)
        .iterations(Integer.MAX_VALUE)
        .rule(
            (g, e, m) -> {
              if (e.attributeKeys().findAny().isEmpty()) {
                g.edges(e)
                    .map(edge -> edge.getOpposite(e))
                    .filter(node -> node.hasAttribute("path") && node.getX() < e.getX())
                    .findFirst()
                    .ifPresent(node -> m.add(e, node));
              }
            },
            (g, match) -> {
              match.get(0).setAttribute("path");
              match.get(1).removeAttribute("path");
              match.get(1).setAttribute("path-resolved");
            }
        )
        .add()

        .random(AbstractApplicationStrategy.ElementType.NODE)
        .rule(
            (g, node, m) -> {
              if (node.hasAttribute("path")) {
                m.add(node);
              }
            },
            (g, match) -> {
              match.get(0).removeAttribute("path");
              match.get(0).setAttribute("end");
            }
        )
        .add()

        .build();

    ruleBook.apply(graph, random);
    return graph;
  }
}