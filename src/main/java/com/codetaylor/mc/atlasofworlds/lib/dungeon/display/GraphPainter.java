package com.codetaylor.mc.atlasofworlds.lib.dungeon.display;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Edge;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Node;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class GraphPainter {

  private final int nodeSpacing;
  private final int nodeScale;
  private final Polygon arrowHead;

  public GraphPainter(int nodeSpacing, int nodeScale) {

    this.nodeSpacing = nodeSpacing;
    this.nodeScale = nodeScale;

    this.arrowHead = new Polygon();
    this.arrowHead.addPoint(0, 0);
    this.arrowHead.addPoint(-this.nodeSpacing / 4, -this.nodeSpacing / 4);
    this.arrowHead.addPoint(this.nodeSpacing / 4, -this.nodeSpacing / 4);
  }

  public void paintNode(Graphics2D graphics, Node node, Point graphOffset, boolean selected) {

    int x = node.getX() * (this.nodeScale + this.nodeSpacing) + this.nodeScale + this.nodeSpacing + graphOffset.x;
    int y = node.getZ() * (this.nodeScale + this.nodeSpacing) + this.nodeScale + this.nodeSpacing + graphOffset.y;

    if (selected) {
      graphics.setPaint(Color.GREEN);

    } else {

      if (node.hasAttribute("start") || node.hasAttribute("end")) {
        graphics.setPaint(Color.ORANGE);

      } else if (node.hasAttribute("path")) {
        graphics.setPaint(Color.PINK);

      } else if (node.hasAttribute("path-resolved")) {
        graphics.setPaint(Color.RED);

      } else {
        graphics.setPaint(Color.GRAY);
      }
    }

    graphics.drawRect(x, y, this.nodeScale, this.nodeScale);
    graphics.drawString(String.format("%s.%s", node.getX(), node.getZ()), x + this.nodeScale / 2, y + this.nodeScale / 2);
  }

  public void paintEdge(Graphics2D graphics, Edge edge, Point graphOffset, boolean selected) {

    Node sourceNode = edge.getSourceNode();
    Node targetNode = edge.getTargetNode();

    int x1 = sourceNode.getX() * (this.nodeScale + this.nodeSpacing) + this.nodeScale + this.nodeSpacing + (this.nodeScale / 2) + graphOffset.x;
    int y1 = sourceNode.getZ() * (this.nodeScale + this.nodeSpacing) + this.nodeScale + this.nodeSpacing + (this.nodeScale / 2) + graphOffset.y;

    int x2 = targetNode.getX() * (this.nodeScale + this.nodeSpacing) + this.nodeScale + this.nodeSpacing + (this.nodeScale / 2) + graphOffset.x;
    int y2 = targetNode.getZ() * (this.nodeScale + this.nodeSpacing) + this.nodeScale + this.nodeSpacing + (this.nodeScale / 2) + graphOffset.y;

    if (x1 > x2) {
      x1 -= (this.nodeScale / 2);
      x2 += (this.nodeScale / 2);

    } else if (x2 > x1) {
      x1 += (this.nodeScale / 2);
      x2 -= (this.nodeScale / 2);
    }

    if (y1 > y2) {
      y1 -= (this.nodeScale / 2);
      y2 += (this.nodeScale / 2);

    } else if (y2 > y1) {
      y1 += (this.nodeScale / 2);
      y2 -= (this.nodeScale / 2);
    }

    if (selected) {
      graphics.setPaint(Color.GREEN);

    } else {
      graphics.setPaint(Color.GRAY);
    }

    graphics.drawLine(x1, y1, x2, y2);

    if (edge.isDirected()) {
      this.paintArrowHead(graphics, x1, y1, x2, y2);
    }
  }

  private void paintArrowHead(Graphics2D graphics, int x1, int y1, int x2, int y2) {

    AffineTransform affineTransform = new AffineTransform();

    affineTransform.setToIdentity();
    double angle = Math.atan2(y2 - y1, x2 - x1);
    affineTransform.translate(x2, y2);
    affineTransform.rotate((angle - Math.PI / 2d));

    Graphics2D g2 = (Graphics2D) graphics.create();
    g2.setTransform(affineTransform);
    g2.fill(this.arrowHead);
    g2.dispose();
  }
}
