package com.absolutesolver.abilities;

import com.absolutesolver.entities.NullEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NullAbility {
	private static final double MAX_RANGE = 64.0;
	private static final float DEFAULT_SIZE = 2.0f;
	
	public static ActionResult use(PlayerEntity player, World world, Hand hand, ItemStack stack) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		
		Vec3d pos = player.getEyePos();
		Vec3d direction = player.getRotationVec(1.0f);
		Vec3d spawnPos = pos.add(direction.multiply(1.5));
		
		NullEntity nullEntity = new NullEntity(world, spawnPos.x, spawnPos.y, spawnPos.z);
		
		// Set size based on sneaking (smaller when sneaking)
		float size = player.isSneaking() ? DEFAULT_SIZE * 0.5f : DEFAULT_SIZE;
		nullEntity.setSize(size);
		
		// Launch it in the direction player is looking
		Vec3d velocity = direction.multiply(0.8);
		nullEntity.setVelocity(velocity);
		nullEntity.velocityModified = true;
		
		world.spawnEntity(nullEntity);
		
		return ActionResult.SUCCESS;
	}
}

