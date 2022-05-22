package com.codetaylor.mc.atlasofworlds.atlas.common.block;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data.service.IBlockEntityDataService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MapDevicePortalBlock
    extends Block
    implements EntityBlock {

  public static final String NAME = "mapdevice_portal";

  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

  private static final VoxelShape SHAPE = Shapes.or(
      Block.box(0, 0, 0, 16, 8, 16)
  );

  private final IBlockEntityDataService blockEntityDataService;

  public MapDevicePortalBlock(IBlockEntityDataService blockEntityDataService) {

    super(Properties.of(Material.STONE, MaterialColor.DEEPSLATE)
        .sound(SoundType.DEEPSLATE_BRICKS)
        .destroyTime(1)
        .explosionResistance(10)
        .noOcclusion()
        .lightLevel(blockState -> blockState.getValue(ACTIVE) ? 6 : 0));

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

    return new MapDevicePortalBlockEntity(blockPos, blockState, this.blockEntityDataService);
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

      if (blockEntity instanceof MapDevicePortalBlockEntity) {
        level.setBlock(blockPos, blockState.setValue(MapDevicePortalBlock.ACTIVE, !blockState.getValue(MapDevicePortalBlock.ACTIVE)), Block.UPDATE_ALL);
      }
    }

    return InteractionResult.SUCCESS;
  }
}
