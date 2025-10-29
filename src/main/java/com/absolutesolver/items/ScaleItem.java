package com.absolutesolver.items;

import com.absolutesolver.abilities.ScaleAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ScaleItem extends Item {
	public ScaleItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		ActionResult result = ScaleAbility.use(user, world, hand, stack);
		return new TypedActionResult<>(result, stack);
	}
}

