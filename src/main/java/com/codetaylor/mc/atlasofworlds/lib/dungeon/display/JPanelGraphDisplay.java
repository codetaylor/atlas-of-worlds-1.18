package com.codetaylor.mc.atlasofworlds.lib.dungeon.display;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JPanelGraphDisplay
    extends JPanel {

  private static final int NODE_SPACING = 20;
  private static final int NODE_SCALE = 40;

  private final GraphPainter graphPainter;

  private final Supplier<Graph> graphSupplier;
  private final Point graphOffset;
  private Element elementSelected;

  public JPanelGraphDisplay(GraphPainter graphPainter, Supplier<Graph> graphSupplier, Consumer<Element> elementConsumer) {

    this.graphPainter = graphPainter;
    this.graphSupplier = graphSupplier;

    this.graphOffset = new Point();

    MouseListener mouseListener = new MouseListener(NODE_SPACING, NODE_SCALE, this.graphSupplier, () -> this.graphOffset,
        element -> {
          this.elementSelected = element;
          this.repaint();
          elementConsumer.accept(this.elementSelected);
        },
        point -> {
          this.graphOffset.setLocation(point);
          this.repaint();
        }
    );
    this.addMouseListener(mouseListener);
    this.addMouseMotionListener(mouseListener);
  }

  @Override
  public void paint(Graphics g) {

    super.paint(g);

    Graphics2D g2 = (Graphics2D) g;

    // Clear
    g2.setPaint(Color.BLACK);
    g2.fillRect(0, 0, getWidth(), getHeight());

    Graph graph = this.graphSupplier.get();
    graph.edges().forEach(edge -> this.graphPainter.paintEdge(g2, edge, this.graphOffset, this.elementSelected == edge));
    graph.nodes().forEach(node -> this.graphPainter.paintNode(g2, node, this.graphOffset, this.elementSelected == node));
  }

  private static class MouseListener
      extends MouseAdapter {

    private final int nodeSpacing;
    private final int nodeScale;
    private final Supplier<Graph> graphSupplier;
    private final Supplier<Point> offsetSupplier;
    private final Consumer<Element> elementConsumer;
    private final Consumer<Point> dragConsumer;
    private final Point origin;
    private final Point dragOffset;
    private final Point offsetOrigin;
    private boolean dragging;

    public MouseListener(int nodeSpacing, int nodeScale, Supplier<Graph> graphSupplier, Supplier<Point> offsetSupplier, Consumer<Element> elementConsumer, Consumer<Point> dragConsumer) {

      this.nodeSpacing = nodeSpacing;
      this.nodeScale = nodeScale;
      this.graphSupplier = graphSupplier;
      this.offsetSupplier = offsetSupplier;
      this.elementConsumer = elementConsumer;
      this.dragConsumer = dragConsumer;
      this.origin = new Point();
      this.dragOffset = new Point();
      this.offsetOrigin = new Point();
    }

    @Override
    public void mousePressed(MouseEvent e) {

      this.dragging = false;
      this.origin.setLocation(e.getPoint());
      this.offsetOrigin.setLocation(this.offsetSupplier.get());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

      if (!this.dragging) {
        Point offset = this.offsetSupplier.get();
        this.updateSelection(e.getX() - offset.x, e.getY() - offset.y);
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

      if (e.getPoint().distanceSq(this.origin) > 16) {
        this.dragging = true;
        this.dragOffset.setLocation(this.offsetOrigin.x + e.getX() - this.origin.x, this.offsetOrigin.y + e.getY() - this.origin.y);
        this.dragConsumer.accept(this.dragOffset);
      }
    }

    private void updateSelection(int x, int y) {

      x -= (this.nodeScale + this.nodeSpacing);
      int col = (x / (this.nodeScale + this.nodeSpacing));
      int localX = x - col * (this.nodeScale + this.nodeSpacing);

      y -= (this.nodeScale + this.nodeSpacing);
      int row = (y / (this.nodeScale + this.nodeSpacing));
      int localY = y - row * (this.nodeScale + this.nodeSpacing);

      if (localX >= 0 && localX < this.nodeScale + this.nodeSpacing
          && localY >= 0 && localY < this.nodeScale + this.nodeSpacing) {

        Graph graph = this.graphSupplier.get();

        if (localX < this.nodeScale && localY < this.nodeScale) {
          this.elementConsumer.accept(graph.getNode(col, 0, row));

        } else if (localX >= this.nodeScale && localY < this.nodeScale) {
          Node nodeSource = graph.getNode(col, 0, row);
          Node nodeTarget = graph.getNode(col + 1, 0, row);
          this.elementConsumer.accept(graph.getEdge(nodeSource, nodeTarget));

        } else if (localX < this.nodeScale) {
          Node nodeSource = graph.getNode(col, 0, row);
          Node nodeTarget = graph.getNode(col, 0, row + 1);
          this.elementConsumer.accept(graph.getEdge(nodeSource, nodeTarget));

        } else {
          this.elementConsumer.accept(null);
        }

      } else {
        this.elementConsumer.accept(null);
      }
    }
  }
}
