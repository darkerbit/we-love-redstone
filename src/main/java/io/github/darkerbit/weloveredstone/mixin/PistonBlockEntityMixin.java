package io.github.darkerbit.weloveredstone.mixin;

import io.github.darkerbit.weloveredstone.block.Blocks;
import io.github.darkerbit.weloveredstone.impl.BlockStateExtensions;
import io.github.darkerbit.weloveredstone.impl.PistonBlockEntityExtensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements PistonBlockEntityExtensions {
	public PistonBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	private NbtCompound weloveredstone$savedBlockEntityNbt = null;

	@Override
	public NbtCompound weloveredstone$getSavedBlockEntityNbt() {
		return weloveredstone$savedBlockEntityNbt;
	}

	@Inject(method="<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)V",
			at = @At("TAIL"))
	private void saveBlockEntityNbt(BlockPos blockPos, BlockState blockState, BlockState movedBlockState, Direction direction,
									  boolean bl, boolean bl2, CallbackInfo ci) {
		if (movedBlockState.isIn(Blocks.MOVEABLE_BLOCK_ENTITIES)) {
			weloveredstone$savedBlockEntityNbt = ((BlockStateExtensions) movedBlockState).weloveredstone$getBlockEntityNbt();
			((BlockStateExtensions) movedBlockState).weloveredstone$setBlockEntityNbt(null);
		}
	}

	@Inject(method="readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
	private void readBlockEntityNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains("weloveredstone$savedBlockEntityNbt", NbtElement.COMPOUND_TYPE)) {
			weloveredstone$savedBlockEntityNbt = nbt.getCompound("weloveredstone$savedBlockEntityNbt");
		}
	}

	@Inject(method="writeNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
	private void writeBlockEntityNbt(NbtCompound nbt, CallbackInfo ci) {
		if (weloveredstone$savedBlockEntityNbt != null) {
			nbt.put("weloveredstone$savedBlockEntityNbt", weloveredstone$savedBlockEntityNbt);
		}
	}

	@Inject(method="tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/PistonBlockEntity;)V",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/World;updateNeighbor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V",
					ordinal = 0))
	private static void insertBlockEntityNbt(World world, BlockPos pos, BlockState state, PistonBlockEntity blockEntity, CallbackInfo ci) {
		NbtCompound nbt = ((PistonBlockEntityExtensions) blockEntity).weloveredstone$getSavedBlockEntityNbt();

		if (nbt != null) {
			BlockEntity be = world.getBlockEntity(pos);

			if (be != null) {
				be.readNbt(nbt);
			}
		}
	}
}
