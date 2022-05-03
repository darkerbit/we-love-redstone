/*
 * Copyright (c) 2022 darkerbit
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.darkerbit.weloveredstone.block;

import io.github.darkerbit.weloveredstone.WeLoveRedstone;
import net.minecraft.block.Blocks;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class AbstractGateBlock extends HorizontalFacingBlock {
	protected static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 2, 16);

	protected final Map<BooleanProperty, Direction> inputs = new HashMap<>();

	protected final Map<Direction, BooleanProperty> outputs = new HashMap<>();

	public AbstractGateBlock(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return hasTopRim(world, pos.down());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		updateNeighbors(state, world, pos);

		// If this needs an evaluation immediately
		if (requiresEvaluate(state, world, pos)) {
			world.scheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);

		// If broken, re-evaluate strong redstone outputs
		if (!moved && !state.isOf(newState.getBlock())) {
			updateNeighbors(state, world, pos);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		// Break if not on anything
		if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		// Schedule an update if necessary
		if (requiresEvaluate(state, world, pos) && !world.getBlockTickScheduler().willTick(pos, this)) {
			world.scheduleBlockTick(pos, this, 2);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockState newState = evaluate(state, world, pos, random);

		world.setBlockState(pos, newState);

		// Perform updates
		updateNeighbors(newState, world, pos);

		// Schedule another update if necessary
		if (requiresEvaluate(newState, world, pos)) {
			world.scheduleBlockTick(pos, this, 2);
		}
	}

	protected void updateNeighbors(BlockState state, World world, BlockPos pos) {
		world.updateNeighbors(pos, this);
		for (var dir : outputs.keySet()) {
			world.updateNeighbors(pos.offset(localToGlobalDirection(state, dir), -1), this);
		}
	}

	// Evaluate inputs and outputs, producing a new BlockState.
	protected abstract BlockState evaluate(BlockState state, ServerWorld world, BlockPos pos, Random random);

	// Does this gate need a re-evaluation?
	public boolean requiresEvaluate(BlockState state, World world, BlockPos pos) {
		for (var entry : inputs.entrySet()) {
			if (state.get(entry.getKey()) != getInput(state, world, pos, entry.getValue())) {
				return true;
			}
		}

		return false;
	}

	// Gets the Redstone signal input at Direction dir.
	public boolean getInput(BlockState state, World world, BlockPos pos, Direction dir) {
		Direction globalDir = localToGlobalDirection(state, dir);
		return world.getEmittedRedstonePower(pos.offset(globalDir, -1), globalDir.getOpposite()) > 0;
	}

	// Gets the Redstone signal input at the direction linked to BooleanProperty dir.
	public boolean getInput(BlockState state, World world, BlockPos pos, BooleanProperty dir) {
		return getInput(state, world, pos, inputs.getOrDefault(dir, Direction.NORTH));
	}

	// Turns a local direction into a global direction.
	public Direction localToGlobalDirection(BlockState state, Direction dir) {
		return Direction.fromHorizontal(dir.getHorizontal() + state.get(FACING).getHorizontal());
	}

	// Turns a global direction into a local direction
	public Direction globalToLocalDirection(BlockState state, Direction dir) {
		return Direction.fromHorizontal(dir.getHorizontal() + 4 - state.get(FACING).getHorizontal());
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		Direction localDir = globalToLocalDirection(state, direction);

		if (!outputs.containsKey(localDir)) {
			return 0;
		}

		return state.get(outputs.get(localDir)) ? 15 : 0;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		String key = WeLoveRedstone.translationKey("block.tooltip", Registry.ITEM.getId(stack.getItem()).getPath());

		// For some reason, Minecraft's translations don't support newlines so I have to implement it myself.
		for (String line : Language.getInstance().get(key).split("\\R")) {
			tooltip.add(new LiteralText(line));
		}
	}
}
