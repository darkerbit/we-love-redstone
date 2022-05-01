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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class DemultiplexerBlock extends AbstractGateBlock {
	private static final BooleanProperty INPUT = BooleanProperty.of("input");
	private static final BooleanProperty SWITCH = BooleanProperty.of("switch");

	private static final BooleanProperty LEFT = BooleanProperty.of("left");
	private static final BooleanProperty RIGHT = BooleanProperty.of("right");

	public DemultiplexerBlock(Settings settings) {
		super(settings);

		inputs.put(INPUT, Direction.NORTH);
		inputs.put(SWITCH, Direction.SOUTH);

		outputs.put(Direction.WEST, LEFT);
		outputs.put(Direction.EAST, RIGHT);

		setDefaultState(getStateManager().getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(INPUT, false)
				.with(SWITCH, false)
				.with(LEFT, false)
				.with(RIGHT, false));
	}

	@Override
	protected BlockState evaluate(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean input = getInput(state, world, pos, INPUT);
		boolean sw = getInput(state, world, pos, SWITCH);

		return state.with(INPUT, input).with(SWITCH, sw).with(LEFT, !sw && input).with(RIGHT, sw && input);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(INPUT);
		builder.add(SWITCH);
		builder.add(LEFT);
		builder.add(RIGHT);
	}
}
