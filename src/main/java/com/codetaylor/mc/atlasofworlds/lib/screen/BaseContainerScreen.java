package com.codetaylor.mc.atlasofworlds.lib.screen;

import com.codetaylor.mc.atlasofworlds.AtlasOfWorldsMod;
import com.codetaylor.mc.atlasofworlds.lib.screen.element.BaseScreenElement;
import com.codetaylor.mc.atlasofworlds.lib.screen.element.IScreenElementClickable;
import com.codetaylor.mc.atlasofworlds.lib.screen.element.IScreenElementTooltipExtendedProvider;
import com.codetaylor.mc.atlasofworlds.lib.screen.element.IScreenElementTooltipProvider;
import com.codetaylor.mc.atlasofworlds.lib.util.TooltipHelper;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseContainerScreen<T extends AbstractContainerMenu>
    extends AbstractContainerScreen<T> {

  protected final List<BaseScreenElement> guiElementList;
  protected final List<IScreenElementClickable> guiElementClickableList;
  protected final List<IScreenElementTooltipProvider> tooltipProviderList;
  protected final List<Component> tooltipTextList;

  protected int scaledWidth;
  protected int scaledHeight;

  public BaseContainerScreen(T container, Inventory playerInventory, MutableComponent title, int width, int height) {

    super(container, playerInventory, title);
    this.imageWidth = width;
    this.imageHeight = height;
    this.guiElementList = new ArrayList<>();
    this.guiElementClickableList = new ArrayList<>();
    this.tooltipProviderList = new ArrayList<>();
    this.tooltipTextList = new ArrayList<>();
    this.updateScaledResolution();
  }

  private void updateScaledResolution() {

    Window window = Minecraft.getInstance().getWindow();
    this.scaledWidth = window.getGuiScaledWidth();
    this.scaledHeight = window.getGuiScaledHeight();
  }

  public Font getFontRenderer() {

    return this.font;
  }

  public ItemRenderer getItemRender() {

    return this.itemRenderer;
  }

  public void drawItemStack(ItemStack itemStack, int x, int y, String altText) {

    if (!itemStack.isEmpty()) {
      this.itemRenderer.renderAndDecorateItem(itemStack, x, y);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, x, y, altText);
    }
  }

  protected void guiContainerElementAdd(BaseScreenElement... elements) {

    for (BaseScreenElement element : elements) {
      this.guiElementList.add(element);

      if (element instanceof IScreenElementClickable) {
        this.guiElementClickableList.add((IScreenElementClickable) element);
      }
    }
  }

  public int guiContainerOffsetXGet() {

    return (this.scaledWidth - this.width) / 2;
  }

  public int guiContainerOffsetYGet() {

    return (this.scaledHeight - this.height) / 2;
  }

  @Override
  public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {

    this.renderBackground(poseStack);
    super.render(poseStack, mouseX, mouseY, partialTicks);
    this.renderTooltip(poseStack, mouseX, mouseY);
  }

  @Override
  protected void renderBg(@Nonnull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {

    this.updateScaledResolution();

    RenderSystem.setShaderColor(1, 1, 1, 1);

    for (BaseScreenElement element : this.guiElementList) {
      element.update(partialTicks);

      if (element.elementIsVisible(mouseX, mouseY)) {
        element.drawBackgroundLayer(poseStack, partialTicks, mouseX, mouseY);
      }
    }
  }

  @Override
  protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {

    super.renderLabels(poseStack, mouseX, mouseY);
    this.tooltipProviderList.clear();

    for (BaseScreenElement element : this.guiElementList) {

      if (element.elementIsVisible(mouseX, mouseY)) {
        element.drawForegroundLayer(poseStack, mouseX, mouseY);

        if (element instanceof IScreenElementTooltipProvider
            && element.elementIsMouseInside(mouseX, mouseY)) {

          this.tooltipProviderList.add((IScreenElementTooltipProvider) element);
        }
      }
    }

    for (IScreenElementTooltipProvider element : this.tooltipProviderList) {
      this.tooltipTextList.clear();

      if (element.elementIsVisible(mouseX, mouseY)
          && element.elementIsMouseInside(mouseX, mouseY)) {
        element.tooltipTextGet(this.tooltipTextList);

        if (element instanceof IScreenElementTooltipExtendedProvider) {

          if (Screen.hasShiftDown()) {
            ((IScreenElementTooltipExtendedProvider) element).tooltipTextExtendedGet(this.tooltipTextList);

          } else {
            this.tooltipTextList.add(TooltipHelper.getTooltipHoldShiftTextComponent(AtlasOfWorldsMod.MOD_ID));
          }
        }

        this.renderTooltip(poseStack, this.tooltipTextList, Optional.empty(), mouseX, mouseY);
      }
    }
  }

  /**
   * Test if the 2D point is in a rectangle (relative to the GUI).
   */
  public boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, double pointX, double pointY) {

    int i = this.leftPos;
    int j = this.topPos;
    pointX = pointX - i;
    pointY = pointY - j;
    return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    boolean result = super.mouseClicked(mouseX, mouseY, button);

    for (IScreenElementClickable element : this.guiElementClickableList) {

      element.mouseClicked(mouseX, mouseY, button);

      if (((BaseScreenElement) element).elementIsMouseInside(mouseX, mouseY)
          && ((BaseScreenElement) element).elementIsVisible(mouseX, mouseY)) {
        element.elementClicked(mouseX, mouseY, button);
      }
    }

    return result;
  }

  public void drawScaledTexturedModalRectFromIcon(PoseStack poseStack, int x, int y, int z, TextureAtlasSprite icon, int width, int height) {

    ScreenHelper.drawScaledTexturedModalRectFromIcon(poseStack, x, y, z, icon, width, height);
  }

  public void drawScaledTexturedModalRectFromIconAnchorBottomLeft(
      PoseStack poseStack,
      int x,
      int y,
      int z,
      TextureAtlasSprite icon,
      int width,
      int height
  ) {

    ScreenHelper.drawScaledTexturedModalRectFromIconAnchorBottomLeft(poseStack, x, y, z, icon, width, height);
  }

  /**
   * Draws a textured square with an optionally rotated texture.
   *
   * @param x        the x
   * @param y        the y
   * @param textureX the texture x
   * @param textureY the texture y
   * @param size     the size
   * @param rotation (clockwise) 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees, 3 = 270 degrees
   */
  public void drawRotatedTexturedModalSquare(PoseStack poseStack, int x, int y, int z, int textureX, int textureY, int size, int rotation) {

    ScreenHelper.drawRotatedTexturedModalSquare(poseStack, x, y, z, textureX, textureY, size, rotation);
  }

  public void drawString(PoseStack poseStack, String text, int x, int y) {

    this.drawString(poseStack, text, x, y, Color.WHITE.getRGB());
  }

  public void drawString(PoseStack poseStack, String text, int x, int y, int color) {

    this.font.draw(poseStack, text, x, y, color);
  }

  public int getGuiLeft() {

    return this.leftPos;
  }

  public int getGuiTop() {

    return this.topPos;
  }

}
