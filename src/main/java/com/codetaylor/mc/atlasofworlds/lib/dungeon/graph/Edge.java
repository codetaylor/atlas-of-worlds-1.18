package com.codetaylor.mc.atlasofworlds.lib.dungeon.graph;

import java.util.Objects;

public class Edge
    extends Element {

  private Node sourceNode;
  private Node targetNode;

  private boolean directed;

  public Edge(Node sourceNode, Node targetNode) {

    this(sourceNode, targetNode, false);
  }

  public Edge(Node sourceNode, Node targetNode, boolean directed) {

    super();

    this.sourceNode = sourceNode;
    this.targetNode = targetNode;
    this.setDirected(directed);
  }

  public void setDirected(boolean directed) {

    this.directed = directed;
  }

  public boolean isDirected() {

    return this.directed;
  }

  public Node getSourceNode() {

    return this.sourceNode;
  }

  public Node getTargetNode() {

    return this.targetNode;
  }

  public Node getOpposite(Node node) {

    if (node == this.sourceNode) {
      return this.targetNode;

    } else if (node == this.targetNode) {
      return this.sourceNode;
    }

    return null;
  }

  public boolean contains(Node node) {

    return this.sourceNode.equals(node) || this.targetNode.equals(node);
  }

  public void reverseDirection() {

    Node sourceNode = this.sourceNode;
    this.sourceNode = this.targetNode;
    this.targetNode = sourceNode;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Edge edge = (Edge) o;
    return this.directed == edge.directed
        && this.sourceNode.equals(edge.sourceNode)
        && this.targetNode.equals(edge.targetNode);
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.sourceNode, this.targetNode, this.directed);
  }

  @Override
  public String toString() {

    return "Edge{" +
        "sourceNode=" + this.sourceNode +
        ", targetNode=" + this.targetNode +
        ", directed=" + this.directed +
        '}';
  }
}
