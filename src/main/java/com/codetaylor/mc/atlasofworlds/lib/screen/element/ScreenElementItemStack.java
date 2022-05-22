package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Supplier;

public class ScreenElementItemStack
    extends BaseScreenElement
    implements IScreenElementTooltipProvider {

  protected final Supplier<ItemStack> itemStackSupplier;
  protected final float alpha;

  public ScreenElementItemStack(
      Supplier<ItemStack> itemStackSupplier,
      float alpha,
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      int elementX, int elementY
  ) {

    super(guiBase, elementX, elementY, 16, 16);
    this.itemStackSupplier = itemStackSupplier;
    this.alpha = alpha;
  }

  @Override
  public void drawBackgroundLayer(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {

    ItemStack stack = this.itemStackSupplier.get();

    if (!stack.isEmpty()) {
//      RenderHelper.enableStandardItemLighting();

      this.guiBase.drawItemStack(
          stack,
          this.elementXModifiedGet(),
          this.elementYModifiedGet(),
          null
      );

      RenderSystem.setShaderColor(1, 1, 1, 1);
//      RenderHelper.disableStandardItemLighting();
    }
  }

  @Override
  public void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY) {
    //
  }

  @Override
  public List<Component> tooltipTextGet(List<Component> tooltip) {

    ItemStack itemStack = this.itemStackSupplier.get();
    Minecraft minecraft = Minecraft.getInstance();

    if (!itemStack.isEmpty()
        && minecraft.player != null
        && minecraft.player.getInventory().getSelected().isEmpty()) {
      TooltipFlag tooltipFlag = minecraft.options.advancedItemTooltips
          ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
      List<Component> itemStackTooltip = itemStack.getTooltipLines(minecraft.player, tooltipFlag);
      tooltip.addAll(itemStackTooltip);
    }

    return tooltip;
  }
}
