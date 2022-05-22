package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.codetaylor.mc.atlasofworlds.lib.screen.ScreenHelper;
import com.codetaylor.mc.atlasofworlds.lib.screen.Texture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ScreenElementTextureRectangle
    extends BaseScreenElementTexture {

  public ScreenElementTextureRectangle(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      Texture texture,
      int elementX,
      int elementY,
      int elementWidth,
      int elementHeight
  ) {

    this(guiBase, new Texture[]{texture}, elementX, elementY, elementWidth, elementHeight);
  }

  public ScreenElementTextureRectangle(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      Texture[] textures,
      int elementX,
      int elementY,
      int elementWidth,
      int elementHeight
  ) {

    super(guiBase, elementX, elementY, elementWidth, elementHeight, textures);
  }

  @Override
  public void drawBackgroundLayer(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {

    Texture texture = this.textureGet(mouseX, mouseY);

    this.textureBind(texture);
    this.elementDraw(poseStack, texture);

  }

  protected void elementDraw(PoseStack poseStack, Texture texture) {

    ScreenHelper.drawModalRectWithCustomSizedTexture(
        poseStack,
        this.elementXModifiedGet(),
        this.elementYModifiedGet(),
        0,
        this.texturePositionXModifiedGet(texture),
        this.texturePositionYModifiedGet(texture),
        this.elementWidthModifiedGet(),
        this.elementHeightModifiedGet(),
        texture.getWidth(),
        texture.getHeight()
    );
  }

  @Override
  public void drawForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
    //
  }

}
