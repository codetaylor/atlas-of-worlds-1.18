package com.codetaylor.mc.atlasofworlds.lib.screen;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public final class ScreenHelper {

  public static final ResourceLocation GALACTIC_ALT_FONT = new ResourceLocation("minecraft", "alt");
  public static final Style GALACTIC_STYLE = Style.EMPTY.withFont(GALACTIC_ALT_FONT);

  public static MutableComponent asGalactic(MutableComponent textComponent) {

    return textComponent.withStyle(GALACTIC_STYLE);
  }

  public static void drawModalRectWithCustomSizedTexture(PoseStack poseStack, int x, int y, int z, float u, float v, int width, int height, float textureWidth, float textureHeight) {

    float f = 1.0F / textureWidth;
    float f1 = 1.0F / textureHeight;
    Matrix4f matrix = poseStack.last().pose();
    Tesselator tesselator = Tesselator.getInstance();
    BufferBuilder bufferbuilder = tesselator.getBuilder();
    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    bufferbuilder.vertex(matrix, x, y + height, z).uv(u * f, (v + (float) height) * f1).endVertex();
    bufferbuilder.vertex(matrix, x + width, y + height, z).uv((u + (float) width) * f, (v + (float) height) * f1).endVertex();
    bufferbuilder.vertex(matrix, x + width, y, z).uv((u + (float) width) * f, v * f1).endVertex();
    bufferbuilder.vertex(matrix, x, y, z).uv(u * f, v * f1).endVertex();
    tesselator.end();
  }

  public static void drawStringOutlined(
      PoseStack poseStack,
      MutableComponent mutableComponent,
      int x,
      int y,
      Font fontRenderer,
      int textShadowColor
  ) {

    ScreenHelper.drawStringOutlined(poseStack, mutableComponent, x, y, fontRenderer, textShadowColor, false);
  }

  public static void drawStringOutlined(
      PoseStack poseStack,
      MutableComponent mutableComponent,
      int x,
      int y,
      Font font,
      int textShadowColor,
      boolean dropShadow
  ) {

    if (dropShadow) {
      font.drawShadow(poseStack, mutableComponent, x + 0, y + 1, textShadowColor);
      font.drawShadow(poseStack, mutableComponent, x + 1, y + 1, textShadowColor);
      font.drawShadow(poseStack, mutableComponent, x + 1, y - 1, textShadowColor);
      font.drawShadow(poseStack, mutableComponent, x + 1, y + 0, textShadowColor);

      font.drawShadow(poseStack, mutableComponent, x - 0, y - 1, textShadowColor);
      font.drawShadow(poseStack, mutableComponent, x - 1, y - 1, textShadowColor);
      font.drawShadow(poseStack, mutableComponent, x - 1, y + 1, textShadowColor);
      font.drawShadow(poseStack, mutableComponent, x - 1, y - 0, textShadowColor);

      font.drawShadow(poseStack, mutableComponent, x, y, Color.BLACK.getRGB());

    } else {
      font.draw(poseStack, mutableComponent, x + 0, y + 1, textShadowColor);
      font.draw(poseStack, mutableComponent, x + 1, y + 1, textShadowColor);
      font.draw(poseStack, mutableComponent, x + 1, y - 1, textShadowColor);
      font.draw(poseStack, mutableComponent, x + 1, y + 0, textShadowColor);

      font.draw(poseStack, mutableComponent, x - 0, y - 1, textShadowColor);
      font.draw(poseStack, mutableComponent, x - 1, y - 1, textShadowColor);
      font.draw(poseStack, mutableComponent, x - 1, y + 1, textShadowColor);
      font.draw(poseStack, mutableComponent, x - 1, y - 0, textShadowColor);

      font.draw(poseStack, mutableComponent, x, y, Color.BLACK.getRGB());
    }
  }

  public static void drawTexturedRect(
      Minecraft minecraft,
      ResourceLocation texture,
      PoseStack poseStack,
      int x,
      int y,
      int width,
      int height,
      int zLevel,
      float u0,
      float v0,
      float u1,
      float v1
  ) {

    TextureManager renderEngine = minecraft.getTextureManager();
    renderEngine.bindForSetup(texture);

    Matrix4f matrix = poseStack.last().pose();
    Tesselator tesselator = Tesselator.getInstance();
    BufferBuilder bufferbuilder = tesselator.getBuilder();
    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    bufferbuilder
        .vertex(matrix, x, (y + height), zLevel)
        .uv(u0, v1)
        .endVertex();
    bufferbuilder
        .vertex(matrix, (x + width), (y + height), zLevel)
        .uv(u1, v1)
        .endVertex();
    bufferbuilder
        .vertex(matrix, (x + width), y, zLevel)
        .uv(u1, v0)
        .endVertex();
    bufferbuilder
        .vertex(matrix, x, y, zLevel)
        .uv(u0, v0)
        .endVertex();
    tesselator.end();
  }

  public static void drawColoredRect(
      BufferBuilder renderer,
      PoseStack poseStack,
      int x,
      int y,
      int width,
      int height,
      int red,
      int green,
      int blue,
      int alpha
  ) {

    Matrix4f matrix = poseStack.last().pose();
    renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    renderer.vertex(matrix, x + 0, y + 0, 0).color(red, green, blue, alpha).endVertex();
    renderer.vertex(matrix, x + 0, y + height, 0).color(red, green, blue, alpha).endVertex();
    renderer.vertex(matrix, x + width, y + height, 0).color(red, green, blue, alpha).endVertex();
    renderer.vertex(matrix, x + width, y + 0, 0).color(red, green, blue, alpha).endVertex();
    Tesselator.getInstance().end();
  }

  public static void drawVerticalScaledTexturedModalRectFromIconAnchorBottomLeft(
      PoseStack poseStack,
      int x,
      int y,
      float z,
      TextureAtlasSprite icon,
      int width,
      int height
  ) {

    // TODO: this only handles tiling vertically, need to implement horizontal tiling as well

    if (icon == null) {
      return;
    }

    int iconHeight = icon.getHeight();
    int iconWidth = icon.getWidth();

    float minU = icon.getU0();
    float maxU = icon.getU1();
    float minV = icon.getV0();
    float maxV = icon.getV1();

    Matrix4f matrix = poseStack.last().pose();
    BufferBuilder buffer = Tesselator.getInstance().getBuilder();
    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

    int sections = height / iconHeight;

    for (int i = 0; i < sections; i++) {
      buffer.vertex(matrix, x, y + height - (i * iconHeight), z)
          .uv(minU, maxV)
          .endVertex();

      buffer.vertex(matrix, x + width, y + height - (i * iconHeight), z)
          .uv(minU + (maxU - minU) * width / (float) iconWidth, maxV)
          .endVertex();

      buffer.vertex(matrix, x + width, y + height - ((i + 1) * iconHeight), z)
          .uv(minU + (maxU - minU) * width / (float) iconWidth, maxV - (maxV - minV))
          .endVertex();

      buffer.vertex(matrix, x, y + height - ((i + 1) * iconHeight), z)
          .uv(minU, maxV - (maxV - minV))
          .endVertex();
    }

    int remainder = height - sections * iconHeight;

    if (remainder > 0) {

      buffer.vertex(matrix, x, y + height - (sections * iconHeight), z)
          .uv(minU, maxV)
          .endVertex();

      buffer.vertex(matrix, x + width, y + height - (sections * iconHeight), z)
          .uv(minU + (maxU - minU) * width / (float) iconWidth, maxV)
          .endVertex();

      buffer.vertex(matrix, x + width, y + height - (sections * iconHeight + remainder), z)
          .uv(minU + (maxU - minU) * width / (float) iconWidth, maxV - (maxV - minV) * remainder / (float) iconHeight)
          .endVertex();

      buffer.vertex(matrix, x, y + height - (sections * iconHeight + remainder), z)
          .uv(minU, maxV - (maxV - minV) * remainder / (float) iconHeight)
          .endVertex();
    }

    /*buffer.pos(x, y + height, z).tex(minU, maxV).endVertex();

    buffer.pos(x + width, y + height, z).tex(minU + (maxU - minU) * width / 16F, maxV).endVertex();

    buffer.pos(x + width, y, z)
        .tex(minU + (maxU - minU) * width / 16F, maxV - (maxV - minV) * height / 16F)
        .endVertex();

    buffer.pos(x, y, z).tex(minU, maxV - (maxV - minV) * height / 16F).endVertex();*/

    Tesselator.getInstance().end();

  }

  public static void drawScaledTexturedModalRectFromIconAnchorBottomLeft(
      PoseStack poseStack,
      int x,
      int y,
      float z,
      TextureAtlasSprite icon,
      int width,
      int height
  ) {

//    double scaledTime = (double) Minecraft.getMinecraft().world.getTotalWorldTime() * 0.05;
//    height += (Math.sin(scaledTime) * 0.5 + 0.5) * 32;
//    width += (Math.sin(scaledTime) * 0.5 + 0.5) * 32;

    if (icon == null) {
      return;
    }

    int iconHeight = icon.getHeight();
    int iconWidth = icon.getWidth();

    float minU = icon.getU0();
    float maxU = icon.getU1();
    float minV = icon.getV0();
    float maxV = icon.getV1();

    int verticalSections = height / iconHeight + 1;
    int horizontalSections = width / iconWidth + 1;

    Matrix4f matrix = poseStack.last().pose();
    BufferBuilder buffer = Tesselator.getInstance().getBuilder();
    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

    for (int i = 0; i < verticalSections; i++) {
      for (int j = 0; j < horizontalSections; j++) {

        int px1 = x + (j * iconWidth);
        int px2 = x + Math.min((j + 1) * iconWidth, width);
        int py1 = y + height - (i * iconHeight);
        int py2 = y + height - Math.min((i + 1) * iconHeight, height);

        float tu2 = minU + (maxU - minU) * (((j + 1) * iconWidth > width)
            ? (width - (j * iconWidth)) / (float) iconWidth : 1);
        float tv2 = maxV - (maxV - minV) * (((i + 1) * iconHeight > height)
            ? (height - (i * iconHeight)) / (float) iconHeight : 1);

        buffer.vertex(matrix, px1, py1, z).uv(minU, maxV).endVertex();
        buffer.vertex(matrix, px2, py1, z).uv(tu2, maxV).endVertex();
        buffer.vertex(matrix, px2, py2, z).uv(tu2, tv2).endVertex();
        buffer.vertex(matrix, px1, py2, z).uv(minU, tv2).endVertex();
      }
    }

    Tesselator.getInstance().end();
  }

  public static int getFluidHeight(int fluidAmount, int fluidCapacity, int displayHeight) {

    float fluidHeightScalar = ScreenHelper.getFluidHeightScalar(fluidAmount, fluidCapacity, displayHeight);
    int elementHeightModified = (int) (fluidHeightScalar * displayHeight);
    return Math.max(0, Math.min(elementHeightModified, displayHeight));
  }

  public static float getFluidHeightScalar(int fluidAmount, int fluidCapacity, int displayHeight) {

    if (fluidAmount > 0) {
      return Math.max((float) fluidAmount / (float) fluidCapacity, 1 / (float) displayHeight);

    } else {
      return 0;
    }
  }

  public static int getFluidY(int fluidAmount, int fluidCapacity, int displayHeight, int offsetY) {

    float fluidHeightScalar = ScreenHelper.getFluidHeightScalar(fluidAmount, fluidCapacity, displayHeight);
    int elementHeightModified = (int) (fluidHeightScalar * displayHeight);
    return displayHeight - Math.max(0, Math.min(elementHeightModified, displayHeight)) + offsetY;
  }

  // https://github.com/TheCBProject/CoFHLib/blob/master/src/main/java/cofh/lib/gui/GuiBase.java
  public static void drawScaledTexturedModalRectFromIcon(
      PoseStack poseStack,
      int x,
      int y,
      float z,
      TextureAtlasSprite icon,
      int width,
      int height
  ) {

    if (icon == null) {
      return;
    }

    int iconHeight = icon.getHeight();
    int iconWidth = icon.getWidth();

    float minU = icon.getU0();
    float maxU = icon.getU1();
    float minV = icon.getV0();
    float maxV = icon.getV1();

    Matrix4f matrix = poseStack.last().pose();
    BufferBuilder buffer = Tesselator.getInstance().getBuilder();
    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    buffer
        .vertex(matrix, x, y + height, z)
        .uv(minU, minV + (maxV - minV) * height / (float) iconHeight)
        .endVertex();
    buffer
        .vertex(matrix, x + width, y + height, z)
        .uv(minU + (maxU - minU) * width / (float) iconWidth, minV + (maxV - minV) * height / (float) iconHeight)
        .endVertex();
    buffer
        .vertex(matrix, x + width, y, z)
        .uv(minU + (maxU - minU) * width / (float) iconWidth, minV)
        .endVertex();
    buffer
        .vertex(matrix, x, y, z)
        .uv(minU, minV)
        .endVertex();
    Tesselator.getInstance().end();
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
  public static void drawRotatedTexturedModalSquare(
      PoseStack poseStack,
      int x,
      int y,
      float z,
      int textureX,
      int textureY,
      int size,
      int rotation
  ) {

    // TODO: these magic numbers tho...

    Matrix4f matrix = poseStack.last().pose();
    Tesselator tesselator = Tesselator.getInstance();
    BufferBuilder bufferBuilder = tesselator.getBuilder();
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

    if (rotation == 1) {

      bufferBuilder
          .vertex(matrix, x, y + size, z)
          .uv((textureX + size) * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, (y + size), z)
          .uv((textureX + size) * 0.00390625F, textureY * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, y, z)
          .uv(textureX * 0.00390625F, textureY * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x, y, z)
          .uv(textureX * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();

    } else if (rotation == 2) {

      bufferBuilder
          .vertex(matrix, x, y + size, z)
          .uv((textureX + size) * 0.00390625F, textureY * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, (y + size), z)
          .uv(textureX * 0.00390625F, textureY * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, y, z)
          .uv(textureX * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x, y, z)
          .uv((textureX + size) * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();

    } else if (rotation == 3) {

      bufferBuilder
          .vertex(matrix, x, y + size, z)
          .uv(textureX * 0.00390625F, textureY * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, (y + size), z)
          .uv(textureX * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, y, z)
          .uv((textureX + size) * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x, y, z)
          .uv((textureX + size) * 0.00390625F, textureY * 0.00390625F)
          .endVertex();

    } else { // rotation 0 is default

      bufferBuilder
          .vertex(matrix, x, y + size, z)
          .uv(textureX * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, (y + size), z)
          .uv((textureX + size) * 0.00390625F, (textureY + size) * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x + size, y, z)
          .uv((textureX + size) * 0.00390625F, textureY * 0.00390625F)
          .endVertex();
      bufferBuilder
          .vertex(matrix, x, y, z)
          .uv(textureX * 0.00390625F, textureY * 0.00390625F)
          .endVertex();

    }

    tesselator.end();

  }

  private ScreenHelper() {
    //
  }

}
