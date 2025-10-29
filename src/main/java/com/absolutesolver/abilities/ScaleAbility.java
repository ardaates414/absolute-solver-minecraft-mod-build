package com.absolutesolver.abilities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ScaleAbility {
	private static final double MAX_RANGE = 48.0;
	private static final float DAMAGE = 20.0f;
	
	public static ActionResult use(PlayerEntity player, World world, Hand hand, ItemStack stack) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		
		HitResult hit = player.raycast(MAX_RANGE, 1.0f, false);
		
		if (hit.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHit = (EntityHitResult) hit;
			Entity target = entityHit.getEntity();
			
			if (target != player && target.getDistance(player) <= MAX_RANGE) {
				damageEntity(player, world, target);
				return ActionResult.SUCCESS;
			}
		} else if (hit.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) hit;
			BlockPos pos = blockHit.getBlockPos();
			BlockState state = world.getBlockState(pos);
			
			if (!state.isAir() && pos.getSquaredDistance(player.getBlockPos()) <= MAX_RANGE * MAX_RANGE) {
				breakBlock(player, world, pos);
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.PASS;
	}
	
	private static void damageEntity(PlayerEntity player, World world, Entity target) {
		if (target instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) target;
			living.damage(world.getDamageSources().magic(), DAMAGE);
			
			// Knockback effect
			Vec3d direction = target.getPos().subtract(player.getEyePos()).normalize();
			target.setVelocity(direction.multiply(0.5));
			target.velocityModified = true;
		} else {
			// For non-living entities, just remove them
			target.remove(Entity.RemovalReason.KILLED);
		}
		
		world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5f, 1.2f);
	}
	
	private static void breakBlock(PlayerEntity player, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.isAir()) return;
		
		if (world instanceof ServerWorld) {
			world.breakBlock(pos, true, player);
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.7f, 1.0f);
		}
	}
}

