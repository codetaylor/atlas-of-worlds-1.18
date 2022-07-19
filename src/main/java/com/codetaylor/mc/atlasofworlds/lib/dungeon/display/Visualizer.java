package com.codetaylor.mc.atlasofworlds.lib.dungeon.display;

import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Element;
import com.codetaylor.mc.atlasofworlds.lib.dungeon.graph.Graph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.function.Supplier;

public class Visualizer {

  private final Supplier<Graph> graphSupplier;

  private JPanel panelRoot;
  private JPanel panelGraphDisplay;
  private JScrollPane scrollPaneAttributeTable;
  private JTable tableElementAttributes;
  private JButton buttonGenerate;

  public Visualizer(IGraphGenerator graphGenerator) {

    this.graphSupplier = graphGenerator;
    this.buttonGenerate.addActionListener(e -> {
      graphGenerator.regenerateGraph();
      this.updateTableModel(null);
      this.panelGraphDisplay.repaint();
    });
  }

  public JPanel getPanelRoot() {

    return this.panelRoot;
  }

  public JPanelGraphDisplay getPanelGraphDisplay() {

    return (JPanelGraphDisplay) this.panelGraphDisplay;
  }

  private void createUIComponents() {

    this.panelGraphDisplay = new JPanelGraphDisplay(new GraphPainter(20, 40), this.graphSupplier, this::updateTableModel);

    this.tableElementAttributes = new JTable();
    this.tableElementAttributes.setDefaultEditor(Object.class, null);

    DefaultTableModel model = (DefaultTableModel) this.tableElementAttributes.getModel();
    model.addColumn("Attribute");
    model.addColumn("Value");
  }

  private void updateTableModel(Element element) {

    DefaultTableModel model = (DefaultTableModel) this.tableElementAttributes.getModel();

    int rowCount = model.getRowCount();

    for (int row = rowCount - 1; row >= 0; row--) {
      model.removeRow(row);
    }

    if (element != null) {
      element.attributeKeys().forEach(attribute -> model.addRow(new Object[]{attribute, element.getAttribute(attribute)}));
    }
  }
}
