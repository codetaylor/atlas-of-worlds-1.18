package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public abstract class BaseScreenElementFluidTank
    extends BaseScreenElementTiledTextureAtlasSprite {

  protected final FluidTank fluidTank;
  private TextureAtlasSprite fluidSprite;

  public BaseScreenElementFluidTank(BaseContainerScreen<? extends AbstractContainerMenu> guiBase, int elementX, int elementY, int elementWidth, int elementHeight, FluidTank fluidTank) {

    super(guiBase, elementX, elementY, elementWidth, elementHeight);
    this.fluidTank = fluidTank;
  }

  protected float scalarPercentageGet() {

    int fluidAmount = this.fluidTank.getFluidAmount();

    if (fluidAmount > 0) {
      int capacity = this.fluidTank.getCapacity();
      return Math.max((float) fluidAmount / (float) capacity, 1 / (float) this.elementHeight);
    }

    return 0;
  }

  @Override
  protected TextureAtlasSprite textureAtlasSpriteGet() {

    FluidStack fluidStack = this.fluidTank.getFluid();

    if (fluidStack == FluidStack.EMPTY) {
      this.fluidSprite = null;

    } else if (this.fluidSprite == null) {
      ResourceLocation resourceLocation = fluidStack.getFluid().getAttributes().getStillTexture();
      this.fluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(resourceLocation);
    }

    return this.fluidSprite;
  }

  @Override
  public void drawBackgroundLayer(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {

    FluidStack fluid = this.fluidTank.getFluid();

    if (fluid != FluidStack.EMPTY) {
      int color = fluid.getFluid().getAttributes().getColor();
      RenderSystem.setShaderColor(
          ((color >> 16) & 0xFF) / 255f,
          ((color >> 8) & 0xFF) / 255f,
          (color & 0xFF) / 255f,
          ((color >> 24) & 0xFF) / 255f
      );
    }

    super.drawBackgroundLayer(poseStack, partialTicks, mouseX, mouseY);

    if (fluid != FluidStack.EMPTY) {
      RenderSystem.setShaderColor(1, 1, 1, 1);
    }
  }
}
