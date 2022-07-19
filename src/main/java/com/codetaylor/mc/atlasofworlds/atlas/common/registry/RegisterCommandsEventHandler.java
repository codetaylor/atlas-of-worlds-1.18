package com.codetaylor.mc.atlasofworlds.atlas.common.registry;

import com.codetaylor.mc.atlasofworlds.Resource;
import com.codetaylor.mc.atlasofworlds.lib.dimension.api.IDimensionManager;
import com.codetaylor.mc.atlasofworlds.lib.level.AtlasMapConfiguration;
import com.codetaylor.mc.atlasofworlds.lib.level.ConfiguredStructureFeatureDecorator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record RegisterCommandsEventHandler(IDimensionManager dimensionManager) {

  @SubscribeEvent
  public void on(RegisterCommandsEvent event) {

    CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

    dispatcher.register(Commands.literal("atlas")
        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
        .then(Commands.literal("test")
            .executes(new TestCommand(this.dimensionManager, this::copyChunkGeneratorWithAutoSeed)))

    );
  }

  private static class TestCommand
      implements Command<CommandSourceStack> {

    private final IDimensionManager dimensionManager;
    private final ChunkGeneratorFactory chunkGeneratorFactory;

    private TestCommand(IDimensionManager dimensionManager, ChunkGeneratorFactory chunkGeneratorFactory) {

      this.dimensionManager = dimensionManager;
      this.chunkGeneratorFactory = chunkGeneratorFactory;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

      /*
      Overriding chunk generator structure set method works.
       */

      ResourceLocation newId = Resource.locate(UUID.randomUUID().toString());
      CommandSourceStack stack = context.getSource();
      MinecraftServer server = stack.getServer();
      ResourceKey<Level> newKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, newId);

      // make sure new dimension's new name isn't already in use
      ServerLevel existingNewLevel = server.getLevel(newKey);
      if (existingNewLevel != null) {
        throw new SimpleCommandExceptionType(new LiteralMessage(String.format("Error copying dimension: ID %s is already in use", newId))).create();
      }

      ResourceKey<Level> resourceKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, Resource.locate("underground"));
      ServerLevel oldLevel = server.getLevel(resourceKey);
      Holder<DimensionType> typeHolder = oldLevel.dimensionTypeRegistration();

      ChunkGenerator generatorCopy = this.chunkGeneratorFactory.create(server, newKey, context, oldLevel);
      Supplier<LevelStem> dimensionFactory = () -> new LevelStem(typeHolder, generatorCopy);

      this.dimensionManager.getOrCreateLevel(server, newKey, dimensionFactory);

      // ---------------------------------------------------------------------------
      // Unregister
      // ---------------------------------------------------------------------------

      // id's of dimensions to unregister
      String[] toUnregister = {

      };

      for (String path : toUnregister) {
        this.dimensionManager.markDimensionForUnregistration(server, ResourceKey.create(Registry.DIMENSION_REGISTRY, Resource.locate(path)));
      }

      ServerLevel level = server.getLevel(newKey);

      if (level == null) {
        throw new SimpleCommandExceptionType(new LiteralMessage("Dimension == null")).create();
      }

      RegistryAccess registryAccess = level.registryAccess();
      ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();
      BiomeSource biomeSource = chunkGenerator.getBiomeSource();
      StructureFeatureManager structureFeatureManager = level.structureFeatureManager();
      StructureManager structureManager = level.getStructureManager();
      ChunkPos chunkPos = ChunkPos.ZERO;
      long seed = level.getSeed();
      int references = 0;

      try {

        Registry<ConfiguredStructureFeature<?, ?>> registry = registryAccess.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        ConfiguredStructureFeature<?, ?> configuredStructureFeature = registry.get(Resource.locate("test"));
        ConfiguredStructureFeatureDecorator<?, ?> configuredStructureFeatureDecorator = ConfiguredStructureFeatureDecorator.decorate(configuredStructureFeature);
        AtlasMapConfiguration atlasMapConfiguration = new AtlasMapConfiguration();
        StructureStart structureStart = configuredStructureFeatureDecorator.generate(registryAccess, chunkGenerator, biomeSource, structureManager, seed, chunkPos, references, level, biomeHolder -> true, atlasMapConfiguration);

        ChunkAccess chunkAccess = level.getChunk(0, 0, ChunkStatus.EMPTY);
        SectionPos sectionPos = SectionPos.bottomOf(chunkAccess);
        structureFeatureManager.setStartForFeature(sectionPos, configuredStructureFeature, structureStart, chunkAccess);

        stack.sendSuccess(new TextComponent("Created dimension with id " + newId), false);

      } catch (Exception e) {
        e.printStackTrace();
        stack.sendFailure(new TextComponent("Failed to create dimension with id " + newId + ", see log for details"));
      }

      return 1;
    }
  }

  private ChunkGenerator copyChunkGeneratorWithAutoSeed(MinecraftServer server, ResourceKey<Level> key, CommandContext<CommandSourceStack> context, ServerLevel oldLevel) {

    long newSeed = server.overworld().getSeed() + key.location().hashCode();
    return oldLevel.getChunkSource().getGenerator().withSeed(newSeed);
  }

  private interface ChunkGeneratorFactory {

    ChunkGenerator create(MinecraftServer server, ResourceKey<Level> key, CommandContext<CommandSourceStack> context, ServerLevel oldLevel);
  }

}
