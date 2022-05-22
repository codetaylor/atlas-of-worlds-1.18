package com.codetaylor.mc.atlasofworlds.lib.screen.element;

public interface IScreenElement {

  boolean elementIsVisible(double mouseX, double mouseY);

  boolean elementIsMouseInside(double mouseX, double mouseY);

}
