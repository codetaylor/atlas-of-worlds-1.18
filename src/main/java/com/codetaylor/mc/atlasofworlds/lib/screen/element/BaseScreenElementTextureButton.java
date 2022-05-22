package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.codetaylor.mc.atlasofworlds.lib.screen.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseScreenElementTextureButton
    extends ScreenElementTextureRectangle
    implements IScreenElementClickable {

  private static final int TEXTURE_BASE_INDEX = 0;
  private static final int TEXTURE_HOVERED_INDEX = 1;

  public BaseScreenElementTextureButton(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      Texture[] textures,
      int elementX,
      int elementY,
      int elementWidth,
      int elementHeight
  ) {

    super(guiBase, textures, elementX, elementY, elementWidth, elementHeight);
  }

  @Override
  protected int textureIndexGet(int mouseX, int mouseY) {

    if (this.elementIsMouseInside(mouseX, mouseY)) {
      return TEXTURE_HOVERED_INDEX;
    }

    return TEXTURE_BASE_INDEX;
  }

  @Override
  public void elementClicked(double mouseX, double mouseY, int mouseButton) {

    Minecraft.getInstance()
        .getSoundManager()
        .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
  }
}
