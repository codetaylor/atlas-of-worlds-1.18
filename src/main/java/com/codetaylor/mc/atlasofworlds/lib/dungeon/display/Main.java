package com.codetaylor.mc.atlasofworlds.lib.dungeon.display;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {

  public static void main(String... args) {

    JFrame frame = new JFrame();
    Dimension dimension = new Dimension(1500, 768);
    frame.setPreferredSize(dimension);

    Visualizer visualizer = new Visualizer(new GraphGenerator(10, 5, new Random()));
    frame.setContentPane(visualizer.getPanelRoot());

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    frame.setLocationRelativeTo(null);

    visualizer.getPanelGraphDisplay().revalidate();
    visualizer.getPanelGraphDisplay().repaint();
  }

}
