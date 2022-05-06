package io.github.darkerbit.weloveredstone.mixin;

import io.github.darkerbit.weloveredstone.block.Blocks;
import io.github.darkerbit.weloveredstone.impl.BlockStateExtensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin extends FacingBlock {
	protected PistonBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method="isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z",
			at = @At("TAIL"), cancellable = true)
	private static void overrideMoveCheck(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
		if (state.isIn(Blocks.MOVEABLE_BLOCK_ENTITIES)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method="move(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Z",
			at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void saveBlockEntity(World world, BlockPos pos, Direction facing, boolean extend, CallbackInfoReturnable<Boolean> cir,
								   BlockPos blockPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedBlocks, List<BlockPos> movedBlockPos,
								   List<BlockState> movedBlockStates, List<BlockPos> brokenBlocks, BlockState[] changedBlockStates, Direction direction,
								   int j, int k, BlockPos newBlockPos, BlockState curBlockState) {
		if (!world.isClient && curBlockState.isIn(Blocks.MOVEABLE_BLOCK_ENTITIES)) {
			BlockEntity be = world.getBlockEntity(movedBlockPos.get(k));

			// Carpet removes the BlockEntity so worth it to check
			if (be != null) {
				((BlockStateExtensions) curBlockState).weloveredstone$setBlockEntityNbt(be.toNbt());
			} else {
				((BlockStateExtensions) curBlockState).weloveredstone$setBlockEntityNbt(null);
			}
		}
	}
}
