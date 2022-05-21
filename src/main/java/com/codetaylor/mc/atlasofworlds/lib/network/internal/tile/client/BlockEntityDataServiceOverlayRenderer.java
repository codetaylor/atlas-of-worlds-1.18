package com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client;

import com.codetaylor.mc.atlasofworlds.lib.network.IClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class BlockEntityDataServiceOverlayRenderer {

  private final IClientConfig clientConfig;

  public BlockEntityDataServiceOverlayRenderer(IClientConfig clientConfig) {

    this.clientConfig = clientConfig;
  }

  public void onRenderGameOverlayPostEvent(RenderGameOverlayEvent.Post event) {

    if (!this.clientConfig.isServiceMonitorEnabled()
        || Minecraft.getInstance().isPaused()) {
      return;
    }

    RenderGameOverlayEvent.ElementType type = event.getType();

    if (type == RenderGameOverlayEvent.ElementType.ALL) {

      int scaledWidth = event.getWindow().getGuiScaledWidth();
      PoseStack matrixStack = event.getMatrixStack();

      // --- Total ---

      this.renderMonitor(matrixStack, BlockEntityDataServiceClientMonitors.getInstance().totalServiceClientMonitor, scaledWidth / 2 - 32 - 128, 100, "Total Rx");

      // --- Position ---

      HitResult traceResult = Minecraft.getInstance().hitResult;

      if (traceResult != null
          && traceResult.getType() == HitResult.Type.BLOCK) {

        BlockPos blockPos = ((BlockHitResult) traceResult).getBlockPos();
        BlockEntityDataServiceClientMonitor monitor = BlockEntityDataServiceClientMonitors.getInstance().findMonitorForPosition(blockPos);

        int x = scaledWidth / 2 - 32 + 128;
        int y = 100;

        if (monitor != null) {
          String title = "[" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + "]";
          this.renderMonitor(matrixStack, monitor, x, y, title);
        }

        BlockEntityDataTrackerUpdateMonitor trackerUpdateMonitor = BlockEntityDataServiceClientMonitors.getInstance().getTrackerUpdateMonitor();
        Object2ObjectArrayMap<BlockPos, Object2IntArrayMap<Class<?>>> updateMap = trackerUpdateMonitor.getPublicTrackerUpdateMap();
        Object2IntArrayMap<Class<?>> map = updateMap.get(blockPos);

        if (map != null) {

          ObjectIterator<Object2IntMap.Entry<Class<?>>> iterator = map.object2IntEntrySet().iterator();
          Font font = Minecraft.getInstance().font;
          int index = 0;

          while (iterator.hasNext()) {
            Object2IntMap.Entry<Class<?>> entry = iterator.next();

            Class<?> dataClass = entry.getKey();
            int count = entry.getIntValue();

            font.drawShadow(matrixStack, dataClass.getSimpleName() + " " + count, x + 64 /* TODO: const */ + 5, y + 9 + index * 10, Color.WHITE.getRGB());
            index += 1;
          }
        }
      }

    }
  }

  public void renderMonitor(PoseStack matrixStack, BlockEntityDataServiceClientMonitor monitor, int x, int y, String title) {

    int trackedIndex = this.clientConfig.getServiceMonitorTrackedIndex();
    int totalWidth = 64; // TODO: const

    int size = monitor.size();

    if (size == 0) {
      return;
    }

    int max = 0;
    int min = Integer.MAX_VALUE;
    int minActual = Integer.MAX_VALUE;
    int total = 0;
    int tracked = 0;

    for (int i = 0; i < size; i++) {
      int count = monitor.get(i);
      total += count;

      if (i == trackedIndex) {
        tracked = count;
      }

      if (count > max) {
        max = count;
      }

      if (count > 0 && count < min) {
        min = count;
      }

      if (count < minActual) {
        minActual = count;
      }
    }

    if (min > max) {
      min = 0;
    }

    Font font = Minecraft.getInstance().font;

    {
      float textWidth = font.getSplitter().stringWidth(title);
      font.drawShadow(matrixStack, title, (float) (x - (textWidth / 2.0) + (totalWidth / 2.0)), y - 9, Color.WHITE.getRGB());
    }

    {
      String text = "§a" + min + " §e" + (int) (total / (float) size) + " §c" + max + " §9" + tracked;
      float textWidth = font.getSplitter().stringWidth(text);
      font.drawShadow(matrixStack, text, (float) (x - (textWidth / 2.0) + (totalWidth / 2.0)), y, Color.WHITE.getRGB());
    }

    if (max == 0) {
      return; // prevent div by zero
    }

    // Only render if the first value
    if (minActual != max) {

      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder renderer = tesselator.getBuilder();

      RenderSystem.disableTexture();
      RenderSystem.enableBlend();

      renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

      int height = 1;
      y += 9;

      int avg = (int) (total / (float) size);

      bufferColoredQuad(renderer, x, y, totalWidth, size, 0, 0, 0, 0.75f);

      int trackedX = 0;
      int innerY = y - 1;
      for (int i = 0; i < size; i++) {

        float widthScalar = monitor.get(i) / (float) max;
        int width = (int) (totalWidth * widthScalar);
        innerY += height;

        if (i == trackedIndex) {
          trackedX = width;

        } else if (width == totalWidth) {
          bufferColoredQuad(renderer, x, innerY, width, height, 1, 0, 0, 0.75f);

        } else if (width < (min / (float) max) * totalWidth + 1) {
          bufferColoredQuad(renderer, x, innerY, width, height, 0, 1, 0, 0.75f);

        } else {
          bufferColoredQuad(renderer, x, innerY, width, height, 1, 1, 1, 0.5f);
        }
      }

      bufferColoredQuad(renderer, (int) (((avg / (float) max) * totalWidth) + x), y, 1, size, 1, 1, 0, 1);
      bufferColoredQuad(renderer, (int) (((min / (float) max) * totalWidth) + x), y, 1, size, 0, 1, 0, 1);
      bufferColoredQuad(renderer, totalWidth + x, y, 1, size, 1, 0, 0, 1);

      if (trackedX > 0) {
        bufferColoredQuad(renderer, x, trackedIndex + y, trackedX, 1, 85 / 255f, 85 / 255f, 1, 1);
        bufferColoredQuad(renderer, (trackedX + x), y, 1, size, 85 / 255f, 85 / 255f, 1, 1);
      }

      tesselator.end();

      RenderSystem.enableTexture();
    }
  }

  private static void bufferColoredQuad(BufferBuilder renderer, int x, int y, float width, int height, float red, float green, float blue, float alpha) {

    renderer.vertex(x, y, 1).color(red, green, blue, alpha).endVertex();
    renderer.vertex(x, y + height, 1).color(red, green, blue, alpha).endVertex();
    renderer.vertex(x + width, y + height, 1).color(red, green, blue, alpha).endVertex();
    renderer.vertex(x + width, y, 1).color(red, green, blue, alpha).endVertex();
  }

}
