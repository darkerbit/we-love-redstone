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
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class OrGateBlock extends AbstractGateBlock {
	private static final BooleanProperty OUT = Properties.POWERED;

	private static final BooleanProperty LEFT = BooleanProperty.of("left");
	private static final BooleanProperty RIGHT = BooleanProperty.of("right");
	private static final BooleanProperty MID = BooleanProperty.of("mid");

	protected OrGateBlock(Settings settings) {
		super(settings);

		outputs.put(Direction.NORTH, OUT);

		inputs.put(LEFT, Direction.WEST);
		inputs.put(RIGHT, Direction.EAST);
		inputs.put(MID, Direction.SOUTH);

		setDefaultState(getStateManager().getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OUT, false)
				.with(LEFT, false)
				.with(RIGHT, false)
				.with(MID, false));
	}

	@Override
	protected BlockState evaluate(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean left = getInput(state, world, pos, LEFT);
		boolean right = getInput(state, world, pos, RIGHT);
		boolean mid = getInput(state, world, pos, MID);

		return state.with(LEFT, left).with(RIGHT, right).with(MID, mid).with(OUT, left || right || mid);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(OUT);
		builder.add(LEFT);
		builder.add(RIGHT);
		builder.add(MID);
	}
}
