package com.ashenmachine.roaming_pets;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoamingPets implements ModInitializer {
	public static final String MOD_ID = "roaming-pets";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) ->
				dispatcher.register(Commands.literal("roaming")
						.then(Commands.argument("radius", IntegerArgumentType.integer(0, 1000))
								.executes(ctx -> {
									int radius = IntegerArgumentType.getInteger(ctx, "radius");
									ServerPlayer player = ctx.getSource().getPlayerOrException();
									RoamingConfig.setRadius(player.getUUID(), radius);
									ctx.getSource().sendSuccess(() -> Component.translatable(
											radius > 0 ? "message.roaming-pets.radius_set" : "message.roaming-pets.radius_unlimited",
											radius), false);
									return radius;
								}))));

		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (hand != InteractionHand.MAIN_HAND || player.isSpectator() || !player.isShiftKeyDown()) {
				return InteractionResult.PASS;
			}
			if (!(entity instanceof TamableAnimal pet) || !(pet instanceof RoamingPet roamer)
					|| !pet.isTame() || !pet.isOwnedBy(player)) {
				return InteractionResult.PASS;
			}
			if (!level.isClientSide()) {
				boolean roaming = !roamer.roamingpets$isRoaming();
				roamer.roamingpets$setRoaming(roaming);
				int radius = RoamingConfig.radius(player.getUUID());
				if (roaming && radius > 0) {
					pet.setHomeTo(pet.blockPosition(), radius);
				} else {
					pet.clearHome();
				}
				pet.getNavigation().stop();
				player.sendOverlayMessage(Component.translatable(
						roaming ? "message.roaming-pets.roaming" : "message.roaming-pets.following", pet.getDisplayName()));
			}
			return InteractionResult.SUCCESS;
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
