package com.absolutesolver.abilities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;

public class RotateAbility {
	private static final double MAX_RANGE = 48.0;
	private static final float ROTATION_SPEED = 15.0f;
	
	public static ActionResult use(PlayerEntity player, World world, Hand hand, ItemStack stack) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		
		HitResult hit = player.raycast(MAX_RANGE, 1.0f, false);
		
		if (hit.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHit = (EntityHitResult) hit;
			Entity target = entityHit.getEntity();
			
			if (target != player && target.getDistance(player) <= MAX_RANGE) {
				rotateEntity(player, world, target);
				return ActionResult.SUCCESS;
			}
		} else if (hit.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) hit;
			BlockPos pos = blockHit.getBlockPos();
			BlockState state = world.getBlockState(pos);
			
			if (!state.isAir() && pos.getSquaredDistance(player.getBlockPos()) <= MAX_RANGE * MAX_RANGE) {
				rotateBlock(player, world, pos);
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.PASS;
	}
	
	private static void rotateEntity(PlayerEntity player, World world, Entity target) {
		Vec3d center = target.getPos();
		Vec3d axis = player.getRotationVec(1.0f).normalize();
		
		// Apply rotation by spinning the entity
		float yaw = target.getYaw() + ROTATION_SPEED;
		target.setYaw(yaw);
		
		// Apply angular velocity for spinning effect
		Vec3d currentVel = target.getVelocity();
		Vec3d tangential = axis.crossProduct(center.subtract(player.getEyePos())).normalize();
		Vec3d newVel = currentVel.add(tangential.multiply(0.3));
		target.setVelocity(newVel);
		target.velocityModified = true;
		
		// If entity is a block entity or structure, break it with rotation
		if (target.getType().getTranslationKey().contains("block")) {
			// Apply damage over time from rotation stress
			if (target instanceof net.minecraft.entity.LivingEntity) {
				((net.minecraft.entity.LivingEntity) target).damage(world.getDamageSources().magic(), 1.0f);
			}
		}
	}
	
	private static void rotateBlock(PlayerEntity player, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		// Try to rotate the block if it supports rotation
		if (state.getProperties().contains(net.minecraft.state.property.Properties.FACING)) {
			Direction current = state.get(net.minecraft.state.property.Properties.FACING);
			Direction next = getNextDirection(current, player.getHorizontalFacing());
			BlockState newState = state.with(net.minecraft.state.property.Properties.FACING, next);
			world.setBlockState(pos, newState);
		} else if (state.getProperties().contains(net.minecraft.state.property.Properties.HORIZONTAL_FACING)) {
			Direction current = state.get(net.minecraft.state.property.Properties.HORIZONTAL_FACING);
			Direction next = getNextHorizontalDirection(current);
			BlockState newState = state.with(net.minecraft.state.property.Properties.HORIZONTAL_FACING, next);
			world.setBlockState(pos, newState);
		} else {
			// If block can't be rotated, break it instead (bending until it breaks)
			world.breakBlock(pos, true, player);
		}
	}
	
	private static Direction getNextDirection(Direction current, Direction playerFacing) {
		Direction[] allDirections = Direction.values();
		int currentIndex = -1;
		for (int i = 0; i < allDirections.length; i++) {
			if (allDirections[i] == current) {
				currentIndex = i;
				break;
			}
		}
		if (currentIndex == -1) return current;
		int nextIndex = (currentIndex + 1) % allDirections.length;
		return allDirections[nextIndex];
	}
	
	private static Direction getNextHorizontalDirection(Direction current) {
		Direction[] horizontal = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
		int currentIndex = -1;
		for (int i = 0; i < horizontal.length; i++) {
			if (horizontal[i] == current) {
				currentIndex = i;
				break;
			}
		}
		if (currentIndex == -1) return current;
		int nextIndex = (currentIndex + 1) % horizontal.length;
		return horizontal[nextIndex];
	}
}

