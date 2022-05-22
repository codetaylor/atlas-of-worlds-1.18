package com.codetaylor.mc.atlasofworlds.lib.screen.element;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface IScreenElementTooltipProvider
    extends IScreenElement {

  List<Component> tooltipTextGet(List<Component> tooltip);

}
