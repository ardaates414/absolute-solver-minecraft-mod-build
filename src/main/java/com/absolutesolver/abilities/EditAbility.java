package com.absolutesolver.abilities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class EditAbility {
	private static final double MAX_RANGE = 48.0;
	
	public static ActionResult use(PlayerEntity player, World world, Hand hand, ItemStack stack) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		
		HitResult hit = player.raycast(MAX_RANGE, 1.0f, false);
		
		if (hit.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHit = (EntityHitResult) hit;
			Entity target = entityHit.getEntity();
			
			if (target != player && target.getDistance(player) <= MAX_RANGE) {
				if (target instanceof ItemEntity) {
					copyItem(player, world, (ItemEntity) target);
				} else if (target instanceof net.minecraft.entity.LivingEntity) {
					repairEntity(player, world, (net.minecraft.entity.LivingEntity) target);
				}
				return ActionResult.SUCCESS;
			}
		} else if (hit.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) hit;
			BlockPos pos = blockHit.getBlockPos();
			BlockState state = world.getBlockState(pos);
			
			if (!state.isAir() && pos.getSquaredDistance(player.getBlockPos()) <= MAX_RANGE * MAX_RANGE) {
				if (player.isSneaking()) {
					dismantleBlock(player, world, pos);
				} else {
					repairOrCopyBlock(player, world, pos);
				}
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.PASS;
	}
	
	private static void copyItem(PlayerEntity player, World world, ItemEntity itemEntity) {
		ItemStack original = itemEntity.getStack();
		ItemStack copy = original.copy();
		copy.setCount(original.getCount());
		
		ItemEntity newItem = new ItemEntity(world, 
			itemEntity.getX() + 0.5, 
			itemEntity.getY() + 0.5, 
			itemEntity.getZ() + 0.5, 
			copy);
		newItem.setVelocity(0, 0.2, 0);
		world.spawnEntity(newItem);
	}
	
	private static void repairEntity(PlayerEntity player, World world, net.minecraft.entity.LivingEntity entity) {
		// Repair damage on living entities
		if (entity.getHealth() < entity.getMaxHealth()) {
			float healAmount = Math.min(10.0f, entity.getMaxHealth() - entity.getHealth());
			entity.heal(healAmount);
		}
		
		// If entity is a player, repair items in inventory
		if (entity instanceof PlayerEntity) {
			PlayerEntity targetPlayer = (PlayerEntity) entity;
			for (ItemStack stack : targetPlayer.getInventory().main) {
				if (stack.isDamaged()) {
					stack.setDamage(Math.max(0, stack.getDamage() - 100));
				}
			}
		}
	}
	
	private static void dismantleBlock(PlayerEntity player, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.isAir()) return;
		
		if (world instanceof ServerWorld) {
			// Break block and drop items
			world.breakBlock(pos, true, player);
		}
	}
	
	private static void repairOrCopyBlock(PlayerEntity player, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.isAir()) return;
		
		// Check if block has damage state (like anvils)
		if (state.getProperties().contains(net.minecraft.state.property.Properties.AXIS)) {
			// Try to repair by resetting damage state
			// Most blocks don't have repair states, so we'll copy instead
		}
		
		// Copy block by placing one nearby
		BlockPos offset = pos.offset(player.getHorizontalFacing());
		if (world.getBlockState(offset).isAir()) {
			world.setBlockState(offset, state);
		}
	}
}

