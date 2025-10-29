package com.absolutesolver.entities;

import com.absolutesolver.AbsoluteSolverMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityTypes {
	public static final EntityType<NullEntity> NULL_ENTITY = Registry.register(
		Registries.ENTITY_TYPE,
		Identifier.of(AbsoluteSolverMod.MOD_ID, "null_entity"),
		FabricEntityTypeBuilder.<NullEntity>create(SpawnGroup.MISC, NullEntity::new)
			.dimensions(EntityDimensions.fixed(2.0f, 2.0f))
			.build()
	);
	
	public static void register() {
		AbsoluteSolverMod.LOGGER.info("Registering Absolute Solver entities");
	}
}

