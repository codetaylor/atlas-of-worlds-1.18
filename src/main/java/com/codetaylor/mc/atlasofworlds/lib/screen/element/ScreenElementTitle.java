package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ScreenElementTitle
    extends BaseScreenElement {

  private final String titleKey;

  public ScreenElementTitle(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      String titleKey,
      int elementX,
      int elementY
  ) {

    // element width and height don't matter
    super(guiBase, elementX, elementY, 0, 0);
    this.titleKey = titleKey;
  }

  @Override
  public void drawBackgroundLayer(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    //
  }

  @Override
  public void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY) {

    this.guiBase.drawString(poseStack, this.titleKey, this.elementX, this.elementY);
  }
}
