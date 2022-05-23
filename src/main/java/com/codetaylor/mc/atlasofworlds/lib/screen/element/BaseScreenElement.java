package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.codetaylor.mc.atlasofworlds.lib.screen.Texture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseScreenElement
    implements IScreenElement {

  protected BaseContainerScreen<? extends AbstractContainerMenu> guiBase;
  protected int elementX;
  protected int elementY;
  protected int elementWidth;
  protected int elementHeight;

  protected boolean visible;

  public BaseScreenElement(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      int elementX,
      int elementY,
      int elementWidth,
      int elementHeight
  ) {

    this.guiBase = guiBase;
    this.elementX = elementX;
    this.elementHeight = elementHeight;
    this.elementY = elementY;
    this.elementWidth = elementWidth;
    this.visible = true;
  }

  public abstract void drawBackgroundLayer(PoseStack poseStack, float partialTicks, int mouseX, int mouseY);

  public abstract void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY);

  public void update(float partialTicks) {
    //
  }

  @Override
  public boolean elementIsVisible(double mouseX, double mouseY) {

    return this.visible;
  }

  public void setVisible(boolean visible) {

    this.visible = visible;
  }

  @Override
  public boolean elementIsMouseInside(double mouseX, double mouseY) {

    return this.guiBase.isPointInRegion(
        this.elementX,
        this.elementY,
        this.elementWidth,
        this.elementHeight,
        mouseX,
        mouseY
    );
  }

  protected void textureBind(ResourceLocation resourceLocation) {

    RenderSystem.setShaderTexture(0, resourceLocation);
  }

  protected void textureBind(Texture texture) {

    this.textureBind(texture.getResourceLocation());
  }

  protected int elementWidthModifiedGet() {

    return this.elementWidth;
  }

  protected int elementHeightModifiedGet() {

    return this.elementHeight;
  }

  protected int elementXModifiedGet() {

    return this.elementX + this.guiBase.guiContainerOffsetXGet();
  }

  protected int elementYModifiedGet() {

    return this.elementY + this.guiBase.guiContainerOffsetYGet();
  }

}
