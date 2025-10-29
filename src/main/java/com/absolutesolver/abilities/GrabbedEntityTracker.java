package com.absolutesolver.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrabbedEntityTracker {
	private static final Map<UUID, UUID> GRABBED_ENTITIES = new HashMap<>();
	
	public static void setGrabbedEntity(PlayerEntity player, Entity entity) {
		if (player.world.isClient) return;
		GRABBED_ENTITIES.put(player.getUuid(), entity.getUuid());
	}
	
	public static Entity getGrabbedEntity(PlayerEntity player) {
		if (player.world.isClient) return null;
		UUID entityUuid = GRABBED_ENTITIES.get(player.getUuid());
		if (entityUuid == null) return null;
		
		for (Entity entity : player.world.getEntities()) {
			if (entity.getUuid().equals(entityUuid)) {
				return entity;
			}
		}
		GRABBED_ENTITIES.remove(player.getUuid());
		return null;
	}
	
	public static void clearGrabbedEntity(PlayerEntity player) {
		if (player.world.isClient) return;
		GRABBED_ENTITIES.remove(player.getUuid());
	}
	
	public static boolean hasGrabbedEntity(PlayerEntity player) {
		return GRABBED_ENTITIES.containsKey(player.getUuid());
	}
}

