package com.codetaylor.mc.atlasofworlds.lib.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public final class TooltipHelper {

  public static final String TOOLTIP_COMMON_HOLD_SHIFT = "gui.%s.tooltip.common.hold_shift";

  public static MutableComponent getTooltipHoldShiftTextComponent(String modId) {

    return new TranslatableComponent(
        String.format(TOOLTIP_COMMON_HOLD_SHIFT, modId),
        ChatFormatting.DARK_GRAY,
        ChatFormatting.AQUA,
        ChatFormatting.DARK_GRAY
    );
  }

  public static void addTooltip(List<String> tooltip, String text, int preferredIndex) {

    if (tooltip.size() > preferredIndex) {
      tooltip.add(preferredIndex, text);

    } else {
      tooltip.add(text);
    }
  }

  private TooltipHelper() {
    //
  }
}
