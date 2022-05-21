package com.codetaylor.mc.atlasofworlds;

import com.codetaylor.mc.atlasofworlds.atlas.AtlasModule;
import com.codetaylor.mc.atlasofworlds.lib.network.ClientSidedProxy;
import com.codetaylor.mc.atlasofworlds.lib.network.CommonSidedProxy;
import com.codetaylor.mc.atlasofworlds.lib.network.internal.tile.TileDataServiceContainer;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AtlasOfWorldsMod.MOD_ID)
public class AtlasOfWorldsMod {

  public static final String MOD_ID = "atlasofworlds";
  // Directly reference a slf4j logger
  private static final Logger LOGGER = LogUtils.getLogger();

  private final AtlasModule atlasModule;

  public AtlasOfWorldsMod() {

    {
      CommonSidedProxy proxy = DistExecutor.unsafeRunForDist(() -> ClientSidedProxy::new, () -> CommonSidedProxy::new);
      proxy.initialize();
      proxy.registerModEventHandlers(FMLJavaModLoadingContext.get().getModEventBus());
      proxy.registerForgeEventHandlers(MinecraftForge.EVENT_BUS);
    }

    this.atlasModule = new AtlasModule(FMLJavaModLoadingContext.get().getModEventBus(), MinecraftForge.EVENT_BUS);



    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the enqueueIMC method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    // Register the processIMC method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
  }

  private void setup(final FMLCommonSetupEvent event) {
    // some preinit code
    LOGGER.info("HELLO FROM PREINIT");
    LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    // Some example code to dispatch IMC to another mod
    InterModComms.sendTo("examplemod", "helloworld", () -> {
      LOGGER.info("Hello world from the MDK");
      return "Hello world";
    });
  }

  private void processIMC(final InterModProcessEvent event) {
    // Some example code to receive and process InterModComms from other mods
    LOGGER.info("Got IMC {}", event.getIMCStream().
        map(m -> m.messageSupplier().get()).
        collect(Collectors.toList()));
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
      // Register a new block here
      LOGGER.info("HELLO from Register Block");
    }
  }
}
