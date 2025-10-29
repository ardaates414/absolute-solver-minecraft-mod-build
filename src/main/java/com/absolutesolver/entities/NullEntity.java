package com.absolutesolver.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;

public class NullEntity extends Entity {
	private float size = 2.0f;
	private int life = 200; // 10 seconds at 20 TPS
	private int expansionTime = 0;
	
	public NullEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setNoGravity(true);
	}
	
	public NullEntity(World world, double x, double y, double z) {
		this(EntityTypes.NULL_ENTITY, world);
		this.setPosition(x, y, z);
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	public float getSize() {
		return size;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (this.world.isClient) {
			// Client-side particle effects
			for (int i = 0; i < 20; i++) {
				double offsetX = (this.random.nextDouble() - 0.5) * size * 2;
				double offsetY = (this.random.nextDouble() - 0.5) * size * 2;
				double offsetZ = (this.random.nextDouble() - 0.5) * size * 2;
				this.world.addParticle(ParticleTypes.PORTAL,
					this.getX() + offsetX,
					this.getY() + offsetY,
					this.getZ() + offsetZ,
					offsetX * 0.1, offsetY * 0.1, offsetZ * 0.1);
				this.world.addParticle(ParticleTypes.END_ROD,
					this.getX() + offsetX * 0.5,
					this.getY() + offsetY * 0.5,
					this.getZ() + offsetZ * 0.5,
					0, 0, 0);
			}
			return;
		}
		
		// Expand gradually
		if (expansionTime < 20) {
			expansionTime++;
			size = Math.min(size + 0.1f, 5.0f);
		}
		
		// Attract and delete nearby entities and items
		Vec3d center = this.getPos();
		double radius = size * 2.0;
		Box box = new Box(
			center.x - radius, center.y - radius, center.z - radius,
			center.x + radius, center.y + radius, center.z + radius
		);
		
		List<Entity> nearby = this.world.getEntitiesByClass(Entity.class, box, (entity) -> {
			return entity != this && !(entity instanceof NullEntity);
		});
		
		for (Entity entity : nearby) {
			double distance = entity.getPos().distanceTo(center);
			if (distance < radius * 0.5) {
				// Delete entity
				entity.remove(Entity.RemovalReason.DISCARDED);
			} else if (distance < radius) {
				// Pull entity towards center
				Vec3d direction = center.subtract(entity.getPos()).normalize();
				double pullStrength = (radius - distance) / radius * 0.3;
				Vec3d velocity = entity.getVelocity().add(direction.multiply(pullStrength));
				entity.setVelocity(velocity);
				entity.velocityModified = true;
			}
		}
		
		// Break nearby blocks
		if (this.world instanceof ServerWorld) {
			net.minecraft.util.math.BlockPos blockPos = this.getBlockPos();
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					for (int z = -2; z <= 2; z++) {
						if (x * x + y * y + z * z <= size * size) {
							net.minecraft.util.math.BlockPos checkPos = blockPos.add(x, y, z);
							if (!this.world.getBlockState(checkPos).isAir() && 
								this.random.nextFloat() < 0.1f) {
								this.world.breakBlock(checkPos, false);
							}
						}
					}
				}
			}
		}
		
		// Despawn after lifetime
		life--;
		if (life <= 0) {
			this.remove(Entity.RemovalReason.DISCARDED);
		}
		
		// Move forward
		if (this.getVelocity().length() > 0) {
			this.setPosition(this.getX() + this.getVelocity().x,
				this.getY() + this.getVelocity().y,
				this.getZ() + this.getVelocity().z);
		}
	}
	
	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putFloat("size", size);
		nbt.putInt("life", life);
		nbt.putInt("expansionTime", expansionTime);
	}
	
	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("size")) {
			size = nbt.getFloat("size");
		}
		if (nbt.contains("life")) {
			life = nbt.getInt("life");
		}
		if (nbt.contains("expansionTime")) {
			expansionTime = nbt.getInt("expansionTime");
		}
	}
	
	@Override
	protected void initDataTracker() {
		// Entity data tracking initialization
	}
}

