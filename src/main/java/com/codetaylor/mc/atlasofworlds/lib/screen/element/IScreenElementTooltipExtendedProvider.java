package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface IScreenElementTooltipExtendedProvider
    extends IScreenElementTooltipProvider {

  List<Component> tooltipTextExtendedGet(List<Component> tooltip);

}
