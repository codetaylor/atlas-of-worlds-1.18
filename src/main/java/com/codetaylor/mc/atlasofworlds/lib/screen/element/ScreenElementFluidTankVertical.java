package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ScreenElementFluidTankVertical
    extends BaseScreenElementFluidTank {

  public ScreenElementFluidTankVertical(
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
  protected int elementHeightModifiedGet() {

    int elementHeightModified = (int) (this.scalarPercentageGet() * this.elementHeight);
    int min = Math.min(elementHeightModified, this.elementHeight);
    return Math.max(0, min);
  }

  @Override
  protected int elementYModifiedGet() {

    int elementHeightModified = (int) (this.scalarPercentageGet() * this.elementHeight);
    int min = Math.min(elementHeightModified, this.elementHeight);
    return this.elementHeight - Math.max(0, min) + super.elementYModifiedGet();
  }
}
