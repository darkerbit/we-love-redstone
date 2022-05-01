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
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public final class Blocks {
	public static final Block OR_GATE_BLOCK = new ThreeInputGateBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER),
			(left, right, mid) -> left || right || mid);
	public static final Block NOR_GATE_BLOCK = new ThreeInputGateBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER),
			(left, right, mid) -> !(left || right || mid));
	public static final Block AND_GATE_BLOCK = new ThreeInputGateBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER),
			(left, right, mid) -> (left || right) && mid);
	public static final Block NAND_GATE_BLOCK = new ThreeInputGateBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER),
			(left, right, mid) -> !((left || right) && mid));
	public static final Block XOR_GATE_BLOCK = new ThreeInputGateBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER),
			(left, right, mid) -> left ^ right ^ mid);
	public static final Block XNOR_GATE_BLOCK = new ThreeInputGateBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER),
			(left, right, mid) -> !(left ^ right ^ mid));

	public static final Block HALF_ADDER_BLOCK = new HalfAdderBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER));

	public static final Block MULTIPLEXER_BLOCK = new ThreeInputGateBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER),
			((left, right, mid) -> mid ? right : left));
	public static final Block DEMULTIPLEXER_BLOCK = new DemultiplexerBlock(QuiltBlockSettings.copyOf(net.minecraft.block.Blocks.REPEATER));

	public static void register() {
		registerBlockWithItem(WeLoveRedstone.identifier("or_gate_block"), OR_GATE_BLOCK);
		registerBlockWithItem(WeLoveRedstone.identifier("nor_gate_block"), NOR_GATE_BLOCK);
		registerBlockWithItem(WeLoveRedstone.identifier("and_gate_block"), AND_GATE_BLOCK);
		registerBlockWithItem(WeLoveRedstone.identifier("nand_gate_block"), NAND_GATE_BLOCK);
		registerBlockWithItem(WeLoveRedstone.identifier("xor_gate_block"), XOR_GATE_BLOCK);
		registerBlockWithItem(WeLoveRedstone.identifier("xnor_gate_block"), XNOR_GATE_BLOCK);

		registerBlockWithItem(WeLoveRedstone.identifier("half_adder_block"), HALF_ADDER_BLOCK);

		registerBlockWithItem(WeLoveRedstone.identifier("multiplexer_block"), MULTIPLEXER_BLOCK);
		registerBlockWithItem(WeLoveRedstone.identifier("demultiplexer_block"), DEMULTIPLEXER_BLOCK);
	}

	private static void registerBlock(Identifier id, Block block) {
		Registry.register(Registry.BLOCK, id, block);
	}

	private static void registerBlockWithItem(Identifier id, Block block) {
		registerBlock(id, block);

		Registry.register(Registry.ITEM, id, new BlockItem(block, new QuiltItemSettings().group(ItemGroup.REDSTONE)));
	}
}
