package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerPowerType;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.PillarmanAction;
import com.github.standobyte.jojo.action.non_stand.PillarmanLightFlash;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PillarmanLightFlash.class, remap = false)
public abstract class PillarmanLightFlashMixin extends PillarmanAction {
    public PillarmanLightFlashMixin(Builder builder) { super(builder); }

    @Inject(method = "startedHolding(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionTarget;Z)V",
    at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;getTypeSpecificData(Lcom/github/standobyte/jojo/power/impl/nonstand/type/NonStandPowerType;)Ljava/util/Optional;"), cancellable = true, remap = false)
    public void fixStartedHolding(World world, LivingEntity user, INonStandPower power, ActionTarget target, boolean requirementsFulfilled, CallbackInfo ci) {
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(true));
            ci.cancel();
        }
    }
    @Inject(method = "stoppedHolding(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;IZ)V",
    at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void fixStoppedHolding(World world, LivingEntity user, INonStandPower power, int ticksHeld, boolean willFire, CallbackInfo ci) {
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(false));
            ci.cancel();
        }
    }
}
