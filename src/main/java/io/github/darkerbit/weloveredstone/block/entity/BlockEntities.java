package io.github.darkerbit.weloveredstone.block.entity;

import io.github.darkerbit.weloveredstone.WeLoveRedstone;
import io.github.darkerbit.weloveredstone.block.Blocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class BlockEntities {
	public static BlockEntityType<BlockPlacerBlockEntity> BLOCK_PLACER_BLOCK_ENTITY;

	public static void register() {
		BLOCK_PLACER_BLOCK_ENTITY = register("block_placer_block_entity", BlockPlacerBlockEntity::new, Blocks.BLOCK_PLACER_BLOCK);
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(Identifier id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory, block).build());
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
		return register(WeLoveRedstone.id(id), factory, block);
	}
}
