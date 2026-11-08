package com.ashenmachine.roaming_pets.mixin;

import com.ashenmachine.roaming_pets.RoamingPet;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin implements RoamingPet {
	@Unique
	private boolean roamingpets$roaming;

	@Override
	public boolean roamingpets$isRoaming() {
		return this.roamingpets$roaming;
	}

	@Override
	public void roamingpets$setRoaming(boolean roaming) {
		this.roamingpets$roaming = roaming;
	}

	// Roaming pets neither follow nor teleport to their owner.
	@Inject(at = @At("HEAD"), method = "unableToMoveToOwner", cancellable = true)
	private void roamingpets$blockFollow(CallbackInfoReturnable<Boolean> cir) {
		if (this.roamingpets$roaming) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
	private void roamingpets$save(ValueOutput output, CallbackInfo ci) {
		output.putBoolean("RoamingPets$Roaming", this.roamingpets$roaming);
	}

	@Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
	private void roamingpets$load(ValueInput input, CallbackInfo ci) {
		this.roamingpets$roaming = input.getBooleanOr("RoamingPets$Roaming", false);
	}
}
