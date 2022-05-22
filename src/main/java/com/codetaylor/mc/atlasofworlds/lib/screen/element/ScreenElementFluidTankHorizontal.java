package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ScreenElementFluidTankHorizontal
    extends BaseScreenElementFluidTank {

  public ScreenElementFluidTankHorizontal(
      BaseContainerScreen<? extends AbstractContainerMenu> guiBase,
      FluidTank fluidTank,
      int elementX,
      int elementY,
      int elementWidth,
      int elementHeight
  ) {

    super(
        guiBase,
        elementX,
        elementY,
        elementWidth,
        elementHeight,
        fluidTank
    );
  }

  @Override
  protected int elementWidthModifiedGet() {

    int elementWidthModified = (int) (this.scalarPercentageGet() * this.elementWidth);
    int min = Math.min(elementWidthModified, this.elementWidth);
    return Math.max(0, min);
  }
}
