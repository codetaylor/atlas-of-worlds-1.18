package com.codetaylor.mc.atlasofworlds.lib.dimension.internal;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IMessage;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ClientboundPacketUpdateDimensions
    implements IMessage<ClientboundPacketUpdateDimensions>,
    IMessageHandler<ClientboundPacketUpdateDimensions, IMessage> {

  private Set<ResourceKey<Level>> keys;
  private boolean add;

  /**
   * @param keys keys to add or remove in the client's dimension list
   * @param add  if true, keys are to be added; if false, keys are to be removed
   */
  public ClientboundPacketUpdateDimensions(Set<ResourceKey<Level>> keys, boolean add) {

    this.keys = keys;
    this.add = add;
  }

  @Override
  public void encode(ClientboundPacketUpdateDimensions message, FriendlyByteBuf packetBuffer) {

    packetBuffer.writeCollection(this.keys, (buf, key) -> buf.writeResourceLocation(key.location()));
    packetBuffer.writeBoolean(this.add);
  }

  @Override
  public ClientboundPacketUpdateDimensions decode(ClientboundPacketUpdateDimensions message, FriendlyByteBuf packetBuffer) {

    message.keys = packetBuffer.readCollection(i -> new HashSet<>(), buf -> ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation()));
    message.add = packetBuffer.readBoolean();
    return message;
  }

  @Override
  public IMessage<ClientboundPacketUpdateDimensions> onMessage(ClientboundPacketUpdateDimensions message, Supplier<NetworkEvent.Context> contextSupplier) {

    final LocalPlayer player = Minecraft.getInstance().player;

    if (player == null) {
      return null;
    }

    final Set<ResourceKey<Level>> dimensionList = player.connection.levels();

    if (dimensionList == null) {
      return null;
    }

    message.keys.forEach((message.add) ? dimensionList::add : dimensionList::remove);

    contextSupplier.get().setPacketHandled(true);

    return null;
  }
}
