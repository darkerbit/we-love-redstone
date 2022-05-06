package io.github.darkerbit.weloveredstone.block;

import net.minecraft.nbt.NbtCompound;

public interface BlockStateExtensions {
	void weloveredstone$setBlockEntityNbt(NbtCompound nbt);
	NbtCompound weloveredstone$getBlockEntityNbt();
}
