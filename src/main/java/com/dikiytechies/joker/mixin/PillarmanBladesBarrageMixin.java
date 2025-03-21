package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.PillarmanAction;
import com.github.standobyte.jojo.action.non_stand.PillarmanBladeBarrage;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PillarmanBladeBarrage.class, remap = false)
public abstract class PillarmanBladesBarrageMixin extends PillarmanAction {
    public PillarmanBladesBarrageMixin(Builder builder) { super(builder); }

    @Inject(method = "startedHolding(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionTarget;Z)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;getTypeSpecificData(Lcom/github/standobyte/jojo/power/impl/nonstand/type/NonStandPowerType;)Ljava/util/Optional;"), cancellable = true, remap = false)
    public void startedHoldingFix(World world, LivingEntity user, INonStandPower power, ActionTarget target, boolean requirementsFulfilled, CallbackInfo ci) {
        power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(joker -> {
            joker.setPillarmanBladesVisible(true);
            ci.cancel();
        });
    }

    @Inject(method = "stoppedHolding(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;IZ)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;getTypeSpecificData(Lcom/github/standobyte/jojo/power/impl/nonstand/type/NonStandPowerType;)Ljava/util/Optional;"), cancellable = true, remap = false)
    public void fixStoppedHolding(World world, LivingEntity user, INonStandPower power, int ticksHeld, boolean willFire, CallbackInfo ci) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(false));
            ci.cancel();
        }
    }
}
