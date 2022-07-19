package com.codetaylor.mc.atlasofworlds.atlas.common;

import com.codetaylor.mc.atlasofworlds.atlas.IAtlasModuleSidedProxy;
import com.codetaylor.mc.atlasofworlds.atlas.common.registry.*;
import com.codetaylor.mc.atlasofworlds.atlas.datagen.GatherDataEventHandler;
import com.codetaylor.mc.atlasofworlds.lib.dimension.api.IDimensionManager;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.packet.IPacketService;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraftforge.eventbus.api.IEventBus;

public class CommonSidedProxy
    implements IAtlasModuleSidedProxy {

  protected final IPacketService packetService;
  protected final IBlockEntityDataService blockEntityDataService;
  protected final IDimensionManager dimensionManager;

  public CommonSidedProxy(IPacketService packetService, IBlockEntityDataService blockEntityDataService, IDimensionManager dimensionManager) {

    this.packetService = packetService;
    this.blockEntityDataService = blockEntityDataService;
    this.dimensionManager = dimensionManager;
  }

  @Override
  public void initialize() {

  }

  @Override
  public void registerModEventHandlers(IEventBus eventBus) {

    GatherDataEventHandler gatherDataEventHandler = new GatherDataEventHandler();
    eventBus.addListener(gatherDataEventHandler::onEvent);

    eventBus.register(new BlockRegistrationEventHandler(this.blockEntityDataService));
    eventBus.register(new ItemRegistrationEventHandler());
    eventBus.register(new BlockEntityRegistrationHandler(this.blockEntityDataService));
    eventBus.register(new MenuTypeRegistrationEventHandler());
    eventBus.register(new StructureFeatureRegistrationEventHandler());
    eventBus.register(new FMLCommonSetupEventHandler());
  }

  @Override
  public void registerForgeEventHandlers(IEventBus eventBus) {

    eventBus.register(new RegisterCommandsEventHandler(this.dimensionManager));
  }
}
