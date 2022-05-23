package com.codetaylor.mc.atlasofworlds.atlas.common.block;

import com.codetaylor.mc.atlasofworlds.atlas.client.screen.MapDeviceScreen;
import com.codetaylor.mc.atlasofworlds.atlas.common.container.MapDeviceContainer;
import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MapDeviceBlock
    extends Block
    implements EntityBlock {

  public static final String NAME = "mapdevice";

  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

  private static final VoxelShape SHAPE = Shapes.or(
      Block.box(2, 0, 2, 14, 2, 14),    // base 1
      Block.box(4, 2, 4, 12, 4, 12),    // base 2
      Block.box(6, 4, 6, 10, 7, 10),    // stem
      Block.box(4, 7, 4, 12, 9, 12),    // base 3
      Block.box(2, 9, 2, 14, 15, 14),   // main
      Block.box(0, 8, 4, 3, 16, 12),    // west
      Block.box(13, 8, 4, 16, 16, 12),  // east
      Block.box(4, 8, 0, 12, 16, 3),    // south
      Block.box(4, 8, 13, 12, 16, 16)   // north
  );

  private final IBlockEntityDataService blockEntityDataService;

  public MapDeviceBlock(IBlockEntityDataService blockEntityDataService) {

    super(Properties.of(Material.STONE, MaterialColor.DEEPSLATE)
        .sound(SoundType.DEEPSLATE_BRICKS)
        .destroyTime(1)
        .explosionResistance(10)
        .noOcclusion()
        .lightLevel(blockState -> blockState.getValue(ACTIVE) ? 10 : 0));

    this.blockEntityDataService = blockEntityDataService;
    this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {

    stateBuilder.add(ACTIVE);
  }

  @ParametersAreNonnullByDefault
  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {

    return new MapDeviceBlockEntity(blockPos, blockState, this.blockEntityDataService);
  }

  @Nonnull
  @Override
  public PushReaction getPistonPushReaction(@Nonnull BlockState blockState) {

    return PushReaction.BLOCK;
  }

  @ParametersAreNonnullByDefault
  @Nonnull
  @Override
  public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {

    return SHAPE;
  }

  @ParametersAreNonnullByDefault
  @Nonnull
  @Override
  public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {

    if (!level.isClientSide) {

      BlockEntity blockEntity = level.getBlockEntity(blockPos);

      if (blockEntity instanceof MapDeviceBlockEntity) {

//        level.setBlock(blockPos, blockState.setValue(MapDeviceBlock.ACTIVE, !blockState.getValue(MapDeviceBlock.ACTIVE)), Block.UPDATE_ALL);

        MenuProvider menuProvider = new MenuProvider() {

          @Nonnull
          @Override
          public Component getDisplayName() {

            return MapDeviceScreen.TITLE_COMPONENT;
          }

          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {

            return new MapDeviceContainer(windowId, blockPos, playerInventory, player);
          }
        };

        NetworkHooks.openGui((ServerPlayer) player, menuProvider, blockPos);
      }
    }

    return InteractionResult.SUCCESS;
  }
}
