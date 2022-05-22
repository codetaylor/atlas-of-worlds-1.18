package com.codetaylor.mc.atlasofworlds.lib.screen.element;

public interface IScreenElementClickable {

  IScreenElementClickable NOOP = new IScreenElementClickable() {
    //
  };

  /**
   * Called when the mouse is clicked, regardless if it is inside the element or not.
   *
   * @param mouseX      mouse x
   * @param mouseY      mouse y
   * @param mouseButton mouse button index
   */
  default void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    //
  }

  /**
   * Called when the mouse is clicked inside the element.
   *
   * @param mouseX      mouse x
   * @param mouseY      mouse y
   * @param mouseButton mouse button index
   */
  default void elementClicked(double mouseX, double mouseY, int mouseButton) {
    //
  }

}
