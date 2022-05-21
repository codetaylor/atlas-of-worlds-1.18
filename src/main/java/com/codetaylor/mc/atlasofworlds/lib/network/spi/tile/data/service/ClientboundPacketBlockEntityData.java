package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service;

import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataServiceContainer;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataServiceLogger;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.BlockEntityDataTracker;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.client.BlockEntityDataServiceClientMonitors;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.ClientboundPacketBlockEntityBase;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IMessage;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.BlockEntityDataContainerBase;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundPacketBlockEntityData
    extends ClientboundPacketBlockEntityBase<ClientboundPacketBlockEntityData> {

  private int serviceId;
  private FriendlyByteBuf buffer;

  @SuppressWarnings("unused")
  public ClientboundPacketBlockEntityData() {
    // serialization
  }

  public ClientboundPacketBlockEntityData(int serviceId, BlockPos origin, FriendlyByteBuf buffer) {

    super(origin);
    this.serviceId = serviceId;
    this.buffer = buffer;
  }

  @Override
  public ClientboundPacketBlockEntityData decode(ClientboundPacketBlockEntityData message, FriendlyByteBuf buffer) {

    super.decode(message, buffer);
    message.serviceId = buffer.readInt();
    int size = buffer.readInt();
    message.buffer = new FriendlyByteBuf(Unpooled.buffer(size));
    buffer.readBytes(message.buffer, size);
    return message;
  }

  @Override
  public void encode(ClientboundPacketBlockEntityData message, FriendlyByteBuf packetBuffer) {

    super.encode(message, packetBuffer);
    packetBuffer.writeInt(message.serviceId);
    packetBuffer.writeInt(message.buffer.writerIndex());
    packetBuffer.writeBytes(message.buffer);
  }

  @Override
  protected IMessage onMessage(ClientboundPacketBlockEntityData message, Supplier<NetworkEvent.Context> contextSupplier, BlockEntity blockEntity) {

    if (blockEntity instanceof BlockEntityDataContainerBase) {

      IBlockEntityDataService dataService = BlockEntityDataServiceContainer.INSTANCE.get().find(message.serviceId);

      if (dataService != null) {
        BlockEntityDataContainerBase containerBase = (BlockEntityDataContainerBase) blockEntity;
        BlockEntityDataTracker tracker = dataService.getTracker(containerBase);

        if (tracker != null) {

          try {
            tracker.updateClient(message.buffer);
            BlockEntityDataServiceClientMonitors.getInstance().onClientPacketReceived(tracker, message.blockPos, message.buffer.writerIndex());
            contextSupplier.get().setPacketHandled(true);

          } catch (Exception e) {
            BlockEntityDataServiceLogger.LOGGER.error("", e);
          }
        }
      }
    }

    return null;
  }
}

