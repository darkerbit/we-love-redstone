package io.github.darkerbit.weloveredstone.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import io.github.darkerbit.weloveredstone.block.BlockStateExtensions;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public abstract class BlockStateMixin extends AbstractBlock.AbstractBlockState implements BlockStateExtensions {
	protected BlockStateMixin(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
		super(block, immutableMap, mapCodec);
	}

	private NbtCompound weloveredstone$savedBlockEntityData;

	@Override
	public void weloveredstone$setBlockEntityNbt(NbtCompound nbt) {
		weloveredstone$savedBlockEntityData = nbt;
	}

	@Override
	public NbtCompound weloveredstone$getBlockEntityNbt() {
		return weloveredstone$savedBlockEntityData;
	}
}
