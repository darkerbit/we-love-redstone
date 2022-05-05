package io.github.darkerbit.weloveredstone.block.entity;

import io.github.darkerbit.weloveredstone.WeLoveRedstone;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BlockPlacerBlockEntity extends LockableContainerBlockEntity {
	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

	public BlockPlacerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntities.BLOCK_PLACER_BLOCK_ENTITY, pos, state);
	}

	@Nullable
	public ItemStack getBlockItem() {
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
				return stack;
			}
		}

		return null;
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText(WeLoveRedstone.translationKey("container", "block_placer"));
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public int size() {
		return 9;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack out = Inventories.splitStack(inventory, slot, amount);

		if (!out.isEmpty()) {
			markDirty();
		}

		return out;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack out = Inventories.removeStack(inventory, slot);

		markDirty();

		return out;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (stack.getCount() > getMaxCountPerStack()) {
			stack.setCount(getMaxCountPerStack());
		}

		inventory.set(slot, stack);

		markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		inventory.clear();

		markDirty();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		Inventories.readNbt(nbt, inventory);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
	}
}
