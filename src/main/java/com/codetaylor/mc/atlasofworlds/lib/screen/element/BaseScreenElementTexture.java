package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.codetaylor.mc.atlasofworlds.lib.screen.Texture;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseScreenElementTexture
    extends BaseScreenElement {

  protected Texture[] textures;

  public BaseScreenElementTexture(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      int elementX,
      int elementY,
      int elementWidth,
      int elementHeight,
      Texture[] textures
  ) {

    super(guiBase, elementX, elementY, elementWidth, elementHeight);
    this.textures = textures;
  }

  protected int texturePositionYModifiedGet(Texture texture) {

    return texture.getPositionY();
  }

  protected int texturePositionXModifiedGet(Texture texture) {

    return texture.getPositionX();
  }

  protected Texture textureGet(int mouseX, int mouseY) {

    return this.textures[this.textureIndexGet(mouseX, mouseY)];
  }

  protected int textureIndexGet(int mouseX, int mouseY) {

    return 0;
  }

}
