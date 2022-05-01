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

public class HalfAdderBlock extends AbstractGateBlock {
	private static final BooleanProperty RESULT = BooleanProperty.of("result");
	private static final BooleanProperty CARRY = BooleanProperty.of("carry");

	private static final BooleanProperty A = BooleanProperty.of("a");
	private static final BooleanProperty B = BooleanProperty.of("b");

	public HalfAdderBlock(Settings settings) {
		super(settings);

		outputs.put(Direction.NORTH, RESULT);
		outputs.put(Direction.WEST, CARRY);

		inputs.put(A, Direction.SOUTH);
		inputs.put(B, Direction.EAST);

		setDefaultState(getStateManager().getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(RESULT, false)
				.with(CARRY, false)
				.with(A, false)
				.with(B, false));
	}

	@Override
	protected BlockState evaluate(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean a = getInput(state, world, pos, A);
		boolean b = getInput(state, world, pos, B);

		return state.with(A, a).with(B, b).with(RESULT, a ^ b).with(CARRY, a && b);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(RESULT);
		builder.add(CARRY);
		builder.add(A);
		builder.add(B);
	}
}
