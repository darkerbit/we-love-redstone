package io.github.darkerbit.weloveredstone.block;

import io.github.darkerbit.weloveredstone.block.entity.BlockPlacerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPlacerBlock extends BlockWithEntity {
	public static class PlacementContext extends AutomaticItemPlacementContext {
		private final Direction facing;

		public PlacementContext(World world, BlockPos blockPos, Direction direction, ItemStack itemStack, Direction direction2) {
			super(world, blockPos, direction, itemStack, direction2);

			facing = direction;
		}

		@Override
		public Direction getPlayerLookDirection() {
			return facing;
		}
	}

	private static final DirectionProperty FACING = FacingBlock.FACING;
	private static final BooleanProperty POWERED = Properties.POWERED;

	public BlockPlacerBlock(Settings settings) {
		super(settings);

		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BlockPlacerBlockEntity(pos, state);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);

			if (factory != null) {
				player.openHandledScreen(factory);
			}
		}

		return ActionResult.SUCCESS;
	}

	private boolean place(BlockState state, ServerWorld world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof BlockPlacerBlockEntity blockEntity) {
			ItemStack stack = blockEntity.getBlockItem();

			if (stack != null && stack.getItem() instanceof BlockItem blockItem) {
				Direction facing = state.get(FACING);
				Direction normal = world.getBlockState(pos.down()).isAir() ? facing : Direction.UP;
				PlacementContext ctx = new PlacementContext(world, pos.offset(facing), facing, stack, normal);

				if (blockItem.place(ctx) != ActionResult.FAIL) {
					blockEntity.markDirty();

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!place(state, world, pos)) {
			world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 0.5f, 1f);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean powered = world.isReceivingRedstonePower(pos);

		if (powered && !state.get(POWERED)) {
			world.scheduleBlockTick(pos, this, 4);
		}

		world.setBlockState(pos, state.with(POWERED, powered), NO_REDRAW);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!newState.isOf(this)) {
			if (world.getBlockEntity(pos) instanceof BlockPlacerBlockEntity blockEntity) {
				ItemScatterer.spawn(world, pos, blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(POWERED);
	}
}
