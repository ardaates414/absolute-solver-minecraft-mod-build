package com.absolutesolver.items;

import com.absolutesolver.AbsoluteSolverMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AbsoluteSolverItems {
	
	public static final Item TRANSLATE_ITEM = new TranslateItem(new FabricItemSettings());
	public static final Item SCALE_ITEM = new ScaleItem(new FabricItemSettings());
	public static final Item ROTATE_ITEM = new RotateItem(new FabricItemSettings());
	public static final Item EDIT_ITEM = new EditItem(new FabricItemSettings());
	public static final Item NULL_ITEM = new NullItem(new FabricItemSettings());
	
	public static void register() {
		Registry.register(Registries.ITEM, Identifier.of(AbsoluteSolverMod.MOD_ID, "translate"), TRANSLATE_ITEM);
		Registry.register(Registries.ITEM, Identifier.of(AbsoluteSolverMod.MOD_ID, "scale"), SCALE_ITEM);
		Registry.register(Registries.ITEM, Identifier.of(AbsoluteSolverMod.MOD_ID, "rotate"), ROTATE_ITEM);
		Registry.register(Registries.ITEM, Identifier.of(AbsoluteSolverMod.MOD_ID, "edit"), EDIT_ITEM);
		Registry.register(Registries.ITEM, Identifier.of(AbsoluteSolverMod.MOD_ID, "null"), NULL_ITEM);
	}
	
	public static void registerAbilities() {
		// Register ability handlers
	}
}

