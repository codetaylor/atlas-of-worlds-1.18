package com.codetaylor.mc.atlasofworlds.lib.dungeon.graph;

import java.util.Objects;

public class Node
    extends Element {

  private final Position position;

  public Node(int x, int y, int z) {

    super();

    this.position = new Position(x, y, z);
  }

  public int getX() {

    return this.position.x;
  }

  public int getY() {

    return this.position.y;
  }

  public int getZ() {

    return this.position.z;
  }

  public Position getPosition() {

    return this.position;
  }

  @Override
  public String toString() {

    return "Node{" +
        "position=" + this.position +
        '}';
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Node node = (Node) o;
    return this.position.equals(node.position);
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.position);
  }

  public static final class Position {

    private int x;
    private int y;
    private int z;

    public Position() {

      this(0, 0, 0);
    }

    public Position(int x, int y, int z) {

      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public String toString() {

      return String.format("[%s, %s, %s]", this.x, this.y, this.z);
    }

    public int x() {

      return this.x;
    }

    public int y() {

      return this.y;
    }

    public int z() {

      return this.z;
    }

    public Position set(int x, int y, int z) {

      this.x = x;
      this.y = y;
      this.z = z;
      return this;
    }

    public Position set(Position position) {

      this.x = position.x;
      this.y = position.y;
      this.z = position.z;
      return this;
    }

    @Override
    public boolean equals(Object obj) {

      if (obj == this) {
        return true;
      }
      if (obj == null || obj.getClass() != this.getClass()) {
        return false;
      }
      Position that = (Position) obj;
      return this.x == that.x &&
          this.y == that.y &&
          this.z == that.z;
    }

    @Override
    public int hashCode() {

      return Objects.hash(x, y, z);
    }

  }
}
