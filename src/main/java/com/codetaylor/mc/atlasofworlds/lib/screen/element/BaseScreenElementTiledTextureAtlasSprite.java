package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;

public abstract class BaseScreenElementTiledTextureAtlasSprite
    extends BaseScreenElement {

  public BaseScreenElementTiledTextureAtlasSprite(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      int elementX,
      int elementY,
      int elementWidth,
      int elementHeight
  ) {

    super(guiBase, elementX, elementY, elementWidth, elementHeight);
  }

  @Override
  public void drawBackgroundLayer(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {

    this.textureBind(InventoryMenu.BLOCK_ATLAS);

    this.guiBase.drawScaledTexturedModalRectFromIconAnchorBottomLeft(
        poseStack,
        this.elementXModifiedGet(),
        this.elementYModifiedGet(),
        0,
        this.textureAtlasSpriteGet(),
        this.elementWidthModifiedGet(),
        this.elementHeightModifiedGet()
    );
  }

  @Override
  public void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY) {
    //
  }

  protected abstract TextureAtlasSprite textureAtlasSpriteGet();
}