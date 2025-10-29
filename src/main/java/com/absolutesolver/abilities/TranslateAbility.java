package com.absolutesolver.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.item.Item;

public class TranslateAbility {
	private static final double MAX_RANGE = 64.0;
	private static final double GRAB_DISTANCE = 32.0;
	
	public static ActionResult use(PlayerEntity player, World world, Hand hand, ItemStack stack) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		
		HitResult hit = player.raycast(MAX_RANGE, 1.0f, false);
		
		// If right-clicking while holding an entity
		if (player.isSneaking()) {
			// Release any grabbed entity
			if (player.getDataTracker().containsKey(GrabbedEntityTracker.GRABBED_ENTITY)) {
				releaseEntity(player);
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS;
		}
		
		if (hit.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHit = (EntityHitResult) hit;
			Entity target = entityHit.getEntity();
			
			if (target != player && target.getDistance(player) <= MAX_RANGE) {
				grabEntity(player, target);
				return ActionResult.SUCCESS;
			}
		} else if (hit.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) hit;
			BlockPos pos = blockHit.getBlockPos();
			BlockState state = world.getBlockState(pos);
			
			if (!state.isAir() && pos.getSquaredDistance(player.getBlockPos()) <= MAX_RANGE * MAX_RANGE) {
				moveBlock(player, world, pos, blockHit.getSide());
				return ActionResult.SUCCESS;
			}
		}
		
		// Throw grabbed entity if one exists
		if (player.getDataTracker().containsKey(GrabbedEntityTracker.GRABBED_ENTITY)) {
			throwEntity(player);
			return ActionResult.SUCCESS;
		}
		
		return ActionResult.PASS;
	}
	
	private static void grabEntity(PlayerEntity player, Entity entity) {
		// Store reference to grabbed entity
		GrabbedEntityTracker.setGrabbedEntity(player, entity);
		entity.setVelocity(Vec3d.ZERO);
		entity.velocityModified = true;
	}
	
	private static void releaseEntity(PlayerEntity player) {
		GrabbedEntityTracker.clearGrabbedEntity(player);
	}
	
	private static void throwEntity(PlayerEntity player) {
		Entity grabbed = GrabbedEntityTracker.getGrabbedEntity(player);
		if (grabbed != null) {
			Vec3d direction = player.getRotationVec(1.0f);
			grabbed.setVelocity(direction.multiply(2.0));
			grabbed.velocityModified = true;
			releaseEntity(player);
		}
	}
	
	private static void moveBlock(PlayerEntity player, World world, BlockPos pos, Direction side) {
		if (!(world instanceof ServerWorld)) return;
		
		BlockState state = world.getBlockState(pos);
		if (state.isAir()) return;
		
		Vec3d direction = player.getRotationVec(1.0f);
		Vec3d offset = direction.multiply(0.5).add(side.getOffsetX() * 0.1, side.getOffsetY() * 0.1, side.getOffsetZ() * 0.1);
		
		// Spawn particles and break block (simulating movement)
		world.breakBlock(pos, true);
	}
	
	public static void tick(PlayerEntity player) {
		Entity grabbed = GrabbedEntityTracker.getGrabbedEntity(player);
		if (grabbed != null && !grabbed.isRemoved()) {
			Vec3d playerPos = player.getEyePos();
			Vec3d direction = player.getRotationVec(1.0f);
			Vec3d targetPos = playerPos.add(direction.multiply(3.0));
			
			Vec3d currentPos = grabbed.getPos();
			Vec3d toTarget = targetPos.subtract(currentPos);
			
			if (toTarget.length() > 0.1) {
				Vec3d velocity = toTarget.multiply(0.3);
				grabbed.setVelocity(velocity);
				grabbed.velocityModified = true;
			} else {
				grabbed.setVelocity(Vec3d.ZERO);
				grabbed.velocityModified = true;
			}
			
			if (grabbed.getDistance(player) > MAX_RANGE) {
				releaseEntity(player);
			}
		}
	}
}

