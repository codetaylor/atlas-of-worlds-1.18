package com.codetaylor.mc.atlasofworlds.lib.dungeon.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Graph
    extends Element {

  private final List<Node> nodeList;
  private final Map<Node.Position, Node> nodeMap;
  private final List<Edge> edgeList;
  private final Map<Node, List<Edge>> edgeMap;

  private final Node.Position nodeLookupKey;

  public Graph(int initialSize) {

    this.nodeList = new ArrayList<>(initialSize);
    this.nodeMap = new HashMap<>(initialSize);
    this.edgeList = new ArrayList<>(initialSize);
    this.edgeMap = new HashMap<>(initialSize);

    this.nodeLookupKey = new Node.Position();
  }

  public void addNode(Node node) {

    this.nodeList.add(node);
    this.nodeMap.put(node.getPosition(), node);
  }

  public void removeNode(Node.Position position) {

    Node node = this.nodeMap.remove(position);

    if (node != null) {
      this.nodeList.remove(node);
    }
  }

  public void removeNode(Node node) {

    this.nodeList.remove(node);
    this.nodeMap.remove(node.getPosition());
  }

  public Node getNode(int x, int y, int z) {

    return this.nodeMap.get(this.nodeLookupKey.set(x, y, z));
  }

  public Stream<Node> nodes() {

    return this.nodeList.stream();
  }

  public void addEdge(Edge edge) {

    this.edgeList.add(edge);

    List<Edge> sourceEdgeList = this.edgeMap.computeIfAbsent(edge.getSourceNode(), node -> new ArrayList<>(1));
    sourceEdgeList.add(edge);

    List<Edge> targetEdgeList = this.edgeMap.computeIfAbsent(edge.getTargetNode(), node -> new ArrayList<>(1));
    targetEdgeList.add(edge);
  }

  public Edge getEdge(Node source, Node target) {

    return this.edges(source)
        .filter(edge -> edge.getOpposite(source).equals(target))
        .findFirst()
        .orElse(null);
  }

  public Stream<Edge> edges(Node node) {

    List<Edge> edgeList = this.edgeMap.get(node);

    if (edgeList != null) {
      return edgeList.stream();
    }

    return Stream.empty();
  }

  public Stream<Edge> edges() {

    return this.edgeList.stream();
  }
}
