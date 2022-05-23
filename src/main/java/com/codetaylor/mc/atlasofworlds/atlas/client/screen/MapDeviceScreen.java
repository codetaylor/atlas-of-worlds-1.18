package com.codetaylor.mc.atlasofworlds.atlas.client.screen;

import com.codetaylor.mc.atlasofworlds.Resource;
import com.codetaylor.mc.atlasofworlds.atlas.common.container.MapDeviceContainer;
import com.codetaylor.mc.atlasofworlds.lib.screen.BaseContainerScreen;
import com.codetaylor.mc.atlasofworlds.lib.screen.Texture;
import com.codetaylor.mc.atlasofworlds.lib.screen.element.ScreenElementTextureRectangle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MapDeviceScreen
    extends BaseContainerScreen<MapDeviceContainer> {

  public static final TranslatableComponent TITLE_COMPONENT = new TranslatableComponent("gui.atlasofworlds.mapdevice.title");

  private static final Texture TEXTURE = new Texture(Resource.locate("textures/gui/map_device.png"), 0, 0, 256, 256);
  private static final int WIDTH = 176;
  private static final int HEIGHT = 206;

  public MapDeviceScreen(MapDeviceContainer container, Inventory playerInventory, Component title) {

    super(container, playerInventory, TITLE_COMPONENT, WIDTH, HEIGHT);

    this.guiContainerElementAdd(new ScreenElementTextureRectangle(
        this,
        TEXTURE,
        0, 0,
        WIDTH, HEIGHT
    ));
  }

  @Override
  protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
    // Prevent the text labels from rendering.
  }
}
