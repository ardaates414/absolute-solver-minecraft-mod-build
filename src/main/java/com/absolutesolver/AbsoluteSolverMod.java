package com.absolutesolver;

import com.absolutesolver.abilities.TranslateAbility;
import com.absolutesolver.entities.EntityTypes;
import com.absolutesolver.items.AbsoluteSolverItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsoluteSolverMod implements ModInitializer {
	public static final String MOD_ID = "absolute-solver";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Absolute Solver Mod");
		
		EntityTypes.register();
		AbsoluteSolverItems.register();
		AbsoluteSolverItems.registerAbilities();
		
		// Register tick handler for Translate ability (to move grabbed entities)
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			world.getPlayers().forEach(player -> {
				if (player.getMainHandStack().getItem() == AbsoluteSolverItems.TRANSLATE_ITEM ||
					player.getOffHandStack().getItem() == AbsoluteSolverItems.TRANSLATE_ITEM) {
					TranslateAbility.tick(player);
				}
			});
		});
	}
}

